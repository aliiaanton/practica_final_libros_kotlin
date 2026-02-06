package com.example.practicafinallibros.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {


    private const val BASE_URL = "http://10.0.2.2:8080/"

    private const val CONTENT_TYPE = "application/json"

    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            json.asConverterFactory(CONTENT_TYPE.toMediaType()))
        .build()

    val api: UsersApi = retrofit.create(UsersApi::class.java)
}