package com.example.sapsmostwantedapp.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val api: OpenSanctionsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://data.opensanctions.org/")
            .addConverterFactory(ScalarsConverterFactory.create()) // NDJSON as raw text
            .client(client)
            .build()
            .create(OpenSanctionsApi::class.java)
    }
}