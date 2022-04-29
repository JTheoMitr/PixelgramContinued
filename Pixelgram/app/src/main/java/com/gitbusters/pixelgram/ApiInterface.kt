package com.gitbusters.pixelgram

import com.gitbusters.pixelgram.api.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    //BACK_END: FETCH ALL POSTS for landing page
    //takes two arguments: Page Number and Page Size
    @GET("posts")
    suspend fun getPosts(
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
    ): Response<MainObject>



    //BACK_END: FETCH COMMENTS for each individual post
    //takes three arguments: Page Number, Page Size, and Post ID
    @GET("posts/{postId}/comments")
    suspend fun getComments(
        @Path("postId") postId: Int,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
    ):Response<CommentObject>


    //BACK_END: SIGN IN with existing account
    @FormUrlEncoded
    @POST("oauth/token")
    fun getTokenData(
        @Field ("username") username: String,
        @Field ("password") password: String
    ):Call<TokenObject>

    //BACK_END: REGISTER new account with Pixelgram
    @FormUrlEncoded
    @POST("oauth/register")
    fun registerUser(
        @Field ("username") username: String,
        @Field ("password") password: String
    ):Call<TokenObject>

    //BACK_END: LOG OUT USER
    @FormUrlEncoded
    @POST("oauth/logout")
    fun logOut(
        @Field ("refresh_token") refresh_token: String
    ):Call<ResponseBody>





}