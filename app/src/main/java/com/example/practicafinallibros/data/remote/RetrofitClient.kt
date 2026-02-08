package com.example.practicafinallibros.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"
    private const val BOOKS_BASE_URL = "https://openlibrary.org/"

    private const val CONTENT_TYPE = "application/json"

    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            json.asConverterFactory(CONTENT_TYPE.toMediaType()))
        .build()

    private val booksRetrofit = Retrofit.Builder()
        .baseUrl(BOOKS_BASE_URL)
        .addConverterFactory(
            json.asConverterFactory(CONTENT_TYPE.toMediaType()))
        .build()

    val usersApi: UsersApi = retrofit.create(UsersApi::class.java)
    val booksApi: BooksApi = booksRetrofit.create(BooksApi::class.java)
}