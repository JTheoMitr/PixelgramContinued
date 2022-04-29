package com.gitbusters.pixelgram.api

data class PostComments(
    var author: Author,
    var createdOn: String,
    var id: Int ,
    var message: String,
    var postId: Int
)
