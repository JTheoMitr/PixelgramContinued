package com.gitbusters.pixelgram

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

const val BASE_URL = "http://34.134.148.105/"

class MainActivity : AppCompatActivity() {
    //BACK_END: Added coroutine scope to project:
    override fun onCreate(savedInstanceState: Bundle?) = runBlocking {
        // Display the logo of the application
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.drawable.ic_pixelgram_logo)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setTitle(" Pixelgram")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCurrentData()
        setLogo()


    }

    //BACK_END: Method to build retrofit instance and create calls
    private fun getCurrentData() {

        //BACK_END: Building our retrofit Builder instance
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        MainScope().launch(Dispatchers.IO) {
            try {
                //BACK_END: Calling our getPosts method from the API Interface
                //BACK_END: .getPosts() takes in pageNumber and pageSize

                val response = api.getPosts(1, 5).awaitResponse()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    Log.d(TAG, data.content.toString())

                    //BACK_END: For POSTS data: the array of incoming posts is data.content
                    //BACK_END: Here we are binding the first post's caption to a TextView to confirm data is flowing properly
                    //BACK_END: TextView can be commented out once data is bound properly with adapter
                    //BACK_END: The data class for Post lives in the api folder
                    withContext(Dispatchers.Main) {
                        // textView.text = data.content[0].message
                        Log.d("DATA", data.content.toString())
                        //FRONT_END Populate the recyclerview
                        rv_post_list.layoutManager = LinearLayoutManager(this@MainActivity)
                        val adapter = PostRecyclerAdapter(data.content)
                        rv_post_list.adapter = adapter
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

    private fun getPostComments() {

        //BACK_END: Building our retrofit Builder instance
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        MainScope().launch(Dispatchers.IO) {
            try {

                //BACK_END: Pass in postId,pageNumber,pageSize

                val response = api.getComments(11,0,5).awaitResponse()
                if (response.isSuccessful) {

                    val data = response.body()!!
                    Log.d(TAG,data.content.toString())

                    withContext(Dispatchers.Main) {
                        //BACK_END: All main thread activity
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

    /* On creation of the app bar */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /* Create the behavior for clicking the toolbar buttons */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        // Skeleton functions for toolbar actions
        R.id.action_new_post -> {
            Toast.makeText(this, "New Post Button press", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.action_profile -> {
            Toast.makeText(this, "account button pressed", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /* Set the app logo on the toolbar */
    fun setLogo() {
        val toolbarTitle = " Pixelgram"
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.drawable.ic_pixelgram_logo)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = toolbarTitle
    }
}