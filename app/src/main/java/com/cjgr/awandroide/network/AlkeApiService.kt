package com.cjgr.awandroide.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AlkeApiService {

    // USERS

    @GET("users")
    suspend fun getUsers(): List<ApiUser>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): ApiUser

    // TRANSACTIONS

    @GET("transactions")
    suspend fun getTransactions(
        @Query("userId") userId: String
    ): List<ApiTransaction>

    @POST("transactions")
    suspend fun createTransaction(
        @Body transaction: ApiTransaction
    ): ApiTransaction
}