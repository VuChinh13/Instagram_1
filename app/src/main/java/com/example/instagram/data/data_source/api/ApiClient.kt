package com.example.instagram.data.data_source.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// retrotfit
object ApiClient {
    private const val BASE_URL = "https://insta.hoibai.net"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
