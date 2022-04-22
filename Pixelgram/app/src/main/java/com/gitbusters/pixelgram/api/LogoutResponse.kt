package com.gitbusters.pixelgram.api

data class LogoutResponse(
    val error: String,
    val message: String,
    val path: String,
    val status: Int,
    val timestamp: String
)