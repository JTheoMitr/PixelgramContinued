package com.gitbusters.pixelgram

import com.gitbusters.pixelgram.api.CommentObject
import com.gitbusters.pixelgram.api.Comments
import com.gitbusters.pixelgram.api.MainObject
import com.gitbusters.pixelgram.api.TokenObject
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @GET("posts")
    fun getPosts(
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
    ): Call<MainObject>



    @GET("posts/{postId}/comments")
    fun getComments(
        @Path("postId") postId: Int,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
    ):Call<CommentObject>


    @FormUrlEncoded
    @POST("oauth/token")
    fun getTokenData(
        @Field ("username") username: String,
        @Field ("password") password: String

    ):Call<TokenObject>

}