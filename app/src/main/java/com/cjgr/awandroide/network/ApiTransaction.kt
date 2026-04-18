package com.cjgr.awandroide.network

data class ApiTransaction(
    val id: String,
    val userId: String,
    val type: String,        // "INGRESO" o "ENVIO"
    val amount: Double,
    val description: String,
    val date: String
)