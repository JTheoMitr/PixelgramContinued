package com.gitbusters.pixelgram

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class CommentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        val postid = intent.getIntExtra("postid", 1)
        getPostComments(postid)

    }

    private fun getPostComments(postid: Int) {
        val rview = findViewById<RecyclerView>(R.id.comment_recyclerview)

        //BACK_END: Building our retrofit Builder instance
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        MainScope().launch(Dispatchers.IO) {
            try {

                //BACK_END: Pass in postId,pageNumber,pageSize

                val response = api.getComments(postid,0,5).awaitResponse()
                if (response.isSuccessful) {

                    val data = response.body()!!
                    Log.d(ContentValues.TAG,data.content.toString())

                    withContext(Dispatchers.Main) {
                        //BACK_END: All main thread activity
                        Log.d("DATA", data.content.toString())
                        //FRONT_END Populate the recyclerview
                        rview.layoutManager = LinearLayoutManager(this@CommentActivity)
                        val adapter = CommentRecyclerAdapter(data)
                        rview.adapter = adapter
                    }
                }

            }
            //BACK_END: Handling call errors
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "no internet", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}