package com.github.ajsnarr98.linknotes.network.http

import com.squareup.moshi.Moshi
import okhttp3.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitBuilder(
    private val callFactory: Call.Factory,
    private val moshi: Moshi,
) {
    fun buildRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .callFactory(callFactory)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}