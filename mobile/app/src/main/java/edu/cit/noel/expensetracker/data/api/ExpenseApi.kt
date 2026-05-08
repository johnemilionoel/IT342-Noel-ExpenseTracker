package edu.cit.noel.expensetracker.data.api

import edu.cit.noel.expensetracker.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ExpenseApi {

    @GET("api/expenses")
    suspend fun getExpenses(@Query("userId") userId: Long): Response<ApiResponse<List<ExpenseResponse>>>

    @POST("api/expenses")
    suspend fun addExpense(@Query("userId") userId: Long, @Body request: ExpenseRequest): Response<ApiResponse<ExpenseResponse>>

    @DELETE("api/expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: Long, @Query("userId") userId: Long): Response<okhttp3.ResponseBody>

    @GET("api/expenses/summary")
    suspend fun getSummary(@Query("userId") userId: Long): Response<ApiResponse<SummaryData>>

    @GET("api/categories")
    suspend fun getCategories(): Response<List<CategoryResponse>>
}
