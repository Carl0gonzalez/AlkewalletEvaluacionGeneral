package com.cjgr.awandroide.model

data class Transaction(
    val id: Int,
    val type: TransactionType,
    val amount: Double,
    val note: String,
    val date: String
)