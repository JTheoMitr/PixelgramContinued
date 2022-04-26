package com.gitbusters.pixelgram.api

data class Content(
    val author: Author,
    val createdOn: String,
    val id: Int,
    val message: String,
    val postId: Int
)