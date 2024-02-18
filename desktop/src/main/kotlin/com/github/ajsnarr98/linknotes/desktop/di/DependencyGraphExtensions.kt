package com.github.ajsnarr98.linknotes.desktop.di

import com.github.ajsnarr98.linknotes.network.http.RetrofitBuilder
import kotlin.reflect.typeOf

/**
 * Helper function for creating a retrofit api. Requires [RetrofitBuilder] dependency.
 */
inline fun <reified T : Any> DependencyGraph.Builder.setNewApi(baseUrl: String) = set<T>(
    dependencies = setOf(typeOf<RetrofitBuilder>()),
    constructor = { deps: DependencyMap ->
        deps.get<RetrofitBuilder>().buildRetrofit(baseUrl).create(T::class.java)
    },
)
