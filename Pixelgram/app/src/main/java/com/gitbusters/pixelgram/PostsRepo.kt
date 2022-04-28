package com.gitbusters.pixelgram

import javax.inject.Inject

class PostsRepo
@Inject constructor(private val apiInterface: ApiInterface) {

        suspend fun getPosts(pageNumber:Int, pageSize:Int) =
        apiInterface.getPosts(pageNumber, pageSize)

        suspend fun getComments(postId:Int, pageNumber:Int, pageSize:Int) =
        apiInterface.getComments(postId, pageNumber, pageSize)

    }