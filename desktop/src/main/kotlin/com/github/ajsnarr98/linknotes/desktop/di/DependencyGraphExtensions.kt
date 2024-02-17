package com.github.ajsnarr98.linknotes.desktop.di

import com.github.ajsnarr98.linknotes.network.http.RetrofitBuilder

/**
 * Helper function for creating a retrofit api. Requires [RetrofitBuilder] dependency.
 */
inline fun <reified T : Any> DependencyGraph.Builder.setNewApi(baseUrl: String) = set(
    clazz = T::class,
    dependencies = setOf(RetrofitBuilder::class),
    constructor = { deps: DependencyMap ->
        deps.get<RetrofitBuilder>().buildRetrofit(baseUrl).create(T::class.java)
    },
)
