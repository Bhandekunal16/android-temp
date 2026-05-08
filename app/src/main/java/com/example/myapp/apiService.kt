package com.example.myapp

import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.GET
import retrofit2.http.Query

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

    @POST("password/delete")
    suspend fun deletePassword(
        @Body body: deleteRequest,
    ): Response<ApiResponse<Unit>>

    @GET("password/get")
    suspend fun getPasswords(
        @Query("id") id: String,
    ): Response<PasswordResponse>

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
