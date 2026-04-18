package com.cjgr.awandroide.network

data class ApiUser(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val balance: Double
)