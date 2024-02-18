package com.github.ajsnarr98.linknotes.desktop.di

import com.github.ajsnarr98.linknotes.desktop.util.appendToSet
import com.github.ajsnarr98.linknotes.desktop.util.removeFromSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KType
import kotlin.reflect.typeOf

typealias DependencyMap = MutableMap<KType, Any>
private typealias ObservableDependencyMap = MutableMap<KType, MutableStateFlow<Any>>
private typealias DependencyConstructorEntry = Pair<Set<KType>, (DependencyMap) -> Any>
private typealias DependencyConstructorMap = MutableMap<KType, DependencyConstructorEntry>

inline fun <reified T> DependencyMap.get(): T = get(typeOf<T>()) as? T
    ?: throw ClassNotFoundException("Dependency map missing class ${typeOf<T>()}")

/**
 * Set this to have the values of other without re-instantiating any flows.
 *
 * Any keys that are missing in other are removed from this.
 */
private fun ObservableDependencyMap.applyFrom(other: DependencyMap) {
    val keysToLose = this.keys - other.keys
    for (key in keysToLose) {
        this.remove(key)
    }

    for ((key, value) in other.entries) {
        if (this.containsKey(key)) {
            this[key]!!.value = value
        } else {
            this[key] = MutableStateFlow(value)
        }
    }
}

private fun ObservableDependencyMap.toDependencyMap(): DependencyMap {
    return this.mapValuesTo(HashMap()) { (_, valueFlow: StateFlow<Any>) -> valueFlow.value }
}

class DependencyGraph {
    private val instantiated: ObservableDependencyMap = mutableMapOf()
    private val constructors: DependencyConstructorMap = mutableMapOf()
    private val topDownRelations: MutableMap<KType, Set<KType>> = mutableMapOf()

    inline fun <reified T : Any> get(): StateFlow<T> = get(typeOf<T>())

    /**
     * This function should only be used directly when one cannot use the inline get().
     */
    fun <T : Any> get(key: KType): StateFlow<T> {
        @Suppress("UNCHECKED_CAST")
        return this.instantiated[key] as? StateFlow<T> ?: throw ClassNotFoundException("Dependency map missing class $key")
    }

    fun setDependencies(block: Builder.() -> Unit): DependencyGraph {
        return Builder(
            instantiatedDependencies = HashMap(this.instantiated.toDependencyMap()),
            constructors = HashMap(this.constructors),
            topDownRelations = HashMap(this.topDownRelations),
        )
            .apply(block)
            .build(this)
    }

    class Builder(
        private val instantiatedDependencies: DependencyMap,
        private val constructors: DependencyConstructorMap,
        private val topDownRelations: MutableMap<KType, Set<KType>>,
    ) {
        private val failedToInstantiate: MutableSet<KType> = mutableSetOf()

        inline fun <reified T : Any> set(noinline constructor: (DependencyMap) -> T) {
            set<T>(
                type = typeOf<T>(),
                dependencies = emptySet(),
                constructor = constructor,
            )
        }

        inline fun <reified T : Any> set(
            dependencies: Set<KType>,
            noinline constructor: (DependencyMap) -> T,
        ) {
            set<T>(
                type = typeOf<T>(),
                dependencies = dependencies,
                constructor = constructor,
            )
        }

        /**
         * Type must match T.
         */
        fun <T : Any> set(
            type: KType,
            dependencies: Set<KType>,
            constructor: (DependencyMap) -> T,
        ): Unit {
            // if there were already any relationships for this entry, clear them
            clear(type, clearConstructor = true)

            for (parentDep in dependencies) {
                topDownRelations.appendToSet(parentDep, type)
            }

            constructors[type] = dependencies to constructor
        }

        /**
         * Clears this dependency and anything depending on it from the graph.
         */
        inline fun <reified T : Any> clear(clearConstructor: Boolean = true) {
            clear(
                type = typeOf<T>(),
                clearConstructor = clearConstructor,
            )
        }

        /**
         * Clears this dependency and anything depending on it from the graph.
         */
        fun clear(type: KType, clearConstructor: Boolean = true) {
            // if there was no constructor, this never got instantiated
            if (!constructors.containsKey(type)) return

            instantiatedDependencies.remove(type)

            // remove any instantiated values for child dependencies that depend on this one
            val childDependencies: Set<KType> = topDownRelations[type] ?: emptySet()
            for (childDep in childDependencies) {
                clear(childDep, clearConstructor = false)
            }

            if (clearConstructor) {
                val constructorEntry = constructors.remove(type)
                val parents: Set<KType> = constructorEntry?.first ?: emptySet()

                // clear stored relationships to parents
                for (parentDep in parents) {
                    topDownRelations.removeFromSet(parentDep, type)
                }
            }
        }

        /**
         * Return true if successful, false if not
         */
        private fun buildDependency(type: KType, constructorInfo: DependencyConstructorEntry): Boolean {
            // make sure all dependencies needed for this constructor are instantiated
            for (parentDep in constructorInfo.first) {
                // check if dep is instantiated already
                if (instantiatedDependencies.containsKey(parentDep)) continue

                // if not, try to instantiate it
                val parentConstructorInfo: DependencyConstructorEntry = constructors[parentDep]
                    ?: run {
                        println("Unable to construct an instance of '$parentDep'. No constructor info added")
                        failedToInstantiate.add(parentDep)
                        return false
                    }

                if (!buildDependency(parentDep, parentConstructorInfo)) {
                    println("Unable to construct an instance of '$parentDep'")
                    failedToInstantiate.add(parentDep)
                    return false
                }
            }
            // instantiate
            instantiatedDependencies[type] = constructorInfo.second(instantiatedDependencies)
            return true
        }

        /**
         * DO NOT call outside of [DependencyGraph] class implementation.
         */
        fun build(outParam: DependencyGraph): DependencyGraph {
            for ((dep: KType, constructorInfo: DependencyConstructorEntry) in constructors.entries) {
                // check if dep is instantiated already or instantiation was attempted and failed
                if (instantiatedDependencies.containsKey(dep) || dep in failedToInstantiate) continue

                if (!buildDependency(dep, constructorInfo)) {
                    println("Unable to construct an instance of '$dep'")
                    failedToInstantiate.add(dep)
                }
            }

            outParam.instantiated.applyFrom(this.instantiatedDependencies)
            outParam.constructors.clear()
            outParam.constructors.putAll(this.constructors)
            outParam.topDownRelations.clear()
            outParam.topDownRelations.putAll(this.topDownRelations)

            return outParam
        }
    }
}