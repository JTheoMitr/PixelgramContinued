package com.gitbusters.pixelgram.api

data class TokenObject(
    val access_token: String,
    val expires_in: Int,
    val refresh_expires_in: Int,
    val refresh_token: String,
    val scope: String,
    val session_state: String,
    val token_type: String,
    val not_before_policy: Int,
)