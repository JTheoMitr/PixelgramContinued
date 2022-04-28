package com.gitbusters.pixelgram

import com.gitbusters.pixelgram.api.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @GET("posts")
    suspend fun getPosts(
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
    ): Response<MainObject>



    @GET("posts/{postId}/comments")
    suspend fun getComments(
        @Path("postId") postId: Int,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
    ):Response<CommentObject>


    @FormUrlEncoded
    @POST("oauth/token")
    fun getTokenData(
        @Field ("username") username: String,
        @Field ("password") password: String

    ):Call<TokenObject>

    @FormUrlEncoded
    @POST("oauth/logout")
    fun logOut(
        @Field ("refresh_token") refresh_token: String
    ):Call<ResponseBody>

}