package edu.cit.noel.expensetracker.data.api

import edu.cit.noel.expensetracker.data.model.ApiResponse
import edu.cit.noel.expensetracker.data.model.LoginRequest
import edu.cit.noel.expensetracker.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<String>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<String>

    @GET("api/auth/user")
    suspend fun getUser(@Query("email") email: String): Response<ApiResponse<Map<String, Any>>>
}