package com.example.practicafinallibros.data.remote

import com.example.practicafinallibros.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UsersApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserDto>

    @GET("api/admin/users")
    suspend fun getAllUsers(
        @Header("Authorization") token: String
    ): Response<List<UserDto>>

    @DELETE("api/admin/users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>
}