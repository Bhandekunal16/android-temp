package com.example.myapp

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // PASSWORD
    @POST("password/save")
    suspend fun savePassword(
        @Body body: Password,
    ): Response<ApiResponse<Unit>>

    @POST("password/update")
    suspend fun updatePassword(
        @Body body: Password,
    ): Response<ApiResponse<Unit>>

    @GET("password/get")
    suspend fun getPasswords(): Response<ApiResponse<List<Password>>>

    // NOTES
    @POST("notes/save")
    suspend fun saveNote(
        @Body body: Note,
    ): Response<ApiResponse<Unit>>

    @POST("notes/update")
    suspend fun updateNote(
        @Body body: Note,
    ): Response<ApiResponse<Unit>>

    @GET("notes/get")
    suspend fun getNotes(): Response<ApiResponse<List<Note>>>

    // AUTH
    @POST("auth/save")
    suspend fun saveAuth(
        @Body body: AuthRequest,
    ): Response<ApiResponse<Unit>>

    @POST("auth/get")
    suspend fun getAuth(
        @Body body: AuthRequest,
    ): Response<ApiResponse<Unit>>
}
