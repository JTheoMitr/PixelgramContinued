package com.gitbusters.pixelgram.api

data class Content(
    val author: AuthorX,
    val createdOn: String,
    val id: Int,
    val message: String,
    val postId: Int
)