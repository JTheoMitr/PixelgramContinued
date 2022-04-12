package com.gitbusters.pixelgram

import android.example.retrofitcoroutinesdemo.api.MainObject
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("posts?pageNumber=1&pageSize=5")
    fun getPosts(): Call<MainObject>
}