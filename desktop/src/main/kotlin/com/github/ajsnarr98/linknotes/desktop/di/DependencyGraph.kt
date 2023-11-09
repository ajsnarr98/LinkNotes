package com.github.ajsnarr98.linknotes.desktop.di

import com.github.ajsnarr98.linknotes.desktop.util.appendToSet
import com.github.ajsnarr98.linknotes.desktop.util.removeFromSet
import kotlin.reflect.KClass

private typealias DependencyMap = MutableMap<KClass<out Any>, Any>
private typealias DependencyConstructorEntry = Pair<Set<KClass<*>>, (DependencyMap) -> Any>
private typealias DependencyConstructorMap = MutableMap<KClass<out Any>, DependencyConstructorEntry>

class DependencyGraph {
    private val instantiated: DependencyMap = mutableMapOf()
    private val constructors: DependencyConstructorMap = mutableMapOf()
    private val topDownRelations: MutableMap<KClass<out Any>, Set<KClass<out Any>>> = mutableMapOf()

    operator fun <T : Any> get(key: KClass<T>): T {
        @Suppress("UNCHECKED_CAST")
        return this.instantiated[key] as? T ?: throw ClassNotFoundException("Dependency map missing class $key")
    }

    fun setDependencies(block: Builder.() -> Unit): DependencyGraph {
        return Builder(
            instantiatedDependencies = HashMap(this.instantiated),
            constructors = HashMap(this.constructors),
            topDownRelations = HashMap(this.topDownRelations),
        )
            .apply(block)
            .build(this)
    }

    class Builder(
        private val instantiatedDependencies: DependencyMap,
        private val constructors: DependencyConstructorMap,
        private val topDownRelations: MutableMap<KClass<out Any>, Set<KClass<out Any>>>,
    ) {
        private val failedToInstantiate: MutableSet<KClass<out Any>> = mutableSetOf()

        fun <T : Any> set(clazz: KClass<T>, constructor: (DependencyMap) -> T) = set(
            clazz = clazz,
            dependencies = emptySet(),
            constructor = constructor,
        )

        fun <T : Any> set(
            clazz: KClass<T>,
            dependencies: Set<KClass<*>>,
            constructor: (DependencyMap) -> T,
        ) {
            // if there were already any relationships for this entry, clear them
            clear(clazz, clearConstructor = true)

            for (parentDep in dependencies) {
                topDownRelations.appendToSet(parentDep, clazz)
            }

            constructors[clazz] = dependencies to constructor
        }

        /**
         * Clears this dependency and anything depending on it from the graph.
         */
        fun clear(clazz: KClass<out Any>, clearConstructor: Boolean = true) {
            // if there was no constructor, this never got instantiated
            if (!constructors.containsKey(clazz)) return

            instantiatedDependencies.remove(clazz)

            // remove any instantiated values for child dependencies that depend on this one
            val childDependencies: Set<KClass<out Any>> = topDownRelations[clazz] ?: emptySet()
            for (childDep in childDependencies) {
                clear(childDep, clearConstructor = false)
            }

            if (clearConstructor) {
                val constructorEntry = constructors.remove(clazz)
                val parents: Set<KClass<out Any>> = constructorEntry?.first ?: emptySet()

                // clear stored relationships to parents
                for (parentDep in parents) {
                    topDownRelations.removeFromSet(parentDep, clazz)
                }
            }
        }

        /**
         * Return true if successful, false if not
         */
        private fun buildDependency(clazz: KClass<out Any>, constructorInfo: DependencyConstructorEntry): Boolean {
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
            instantiatedDependencies[clazz] = constructorInfo.second(instantiatedDependencies)
            return true
        }

        /**
         * DO NOT call outside of [DependencyGraph] class implementation.
         */
        fun build(outParam: DependencyGraph): DependencyGraph {
            for ((dep: KClass<out Any>, constructorInfo: DependencyConstructorEntry) in constructors.entries) {
                // check if dep is instantiated already or instantiation was attempted and failed
                if (instantiatedDependencies.containsKey(dep) || dep in failedToInstantiate) continue

                if (!buildDependency(dep, constructorInfo)) {
                    println("Unable to construct an instance of '$dep'")
                    failedToInstantiate.add(dep)
                }
            }

            outParam.instantiated.clear()
            outParam.instantiated.putAll(this.instantiatedDependencies)
            outParam.constructors.clear()
            outParam.constructors.putAll(this.constructors)
            outParam.topDownRelations.clear()
            outParam.topDownRelations.putAll(this.topDownRelations)

            return outParam
        }
    }
}