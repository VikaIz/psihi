package com.izm.spokoyno.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {
    // Базовый URL до raw-контента GitHub
    private const val BASE_URL = "https://raw.githubusercontent.com/"

    val api: TestApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TestApi::class.java)
    }
}