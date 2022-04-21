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
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.gitbusters.pixelgram.api.Post
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

const val BASE_URL = "http://34.134.148.105/"

class MainActivity : AppCompatActivity() {

    lateinit var layoutManager: LinearLayoutManager
    //lateinit var adapter: PostRecyclerAdapter
    var adapter = PostRecyclerAdapter(listOf()) // listOf<Post>()
    var page = 1
    //var limit = 10
    var isLoading = false

    //BACK_END: Added coroutine scope to project:
    override fun onCreate(savedInstanceState: Bundle?) = runBlocking {

        // Display the logo of the application
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.drawable.ic_pixelgram_logo)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setTitle(" Pixelgram")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_post_list)
       // recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        updateCurrentData(page, adapter)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    page++
                    updateCurrentData(page, adapter)
                    Toast.makeText(this@MainActivity, "$page", Toast.LENGTH_LONG).show()
                }
            }
        })
        setLogo()
    }

    //BACK_END: Method to build retrofit instance and create calls
    private fun updateCurrentData(pn : Int, adapter: PostRecyclerAdapter) {

        //BACK_END: Building our retrofit Builder instance
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        MainScope().launch(Dispatchers.IO) {
            try {

                val response = api.getPosts(pn, 5).awaitResponse()

                if (response.isSuccessful) {
                    val data = response.body()!!
                    Log.d(TAG, data.content.toString())

                    withContext(Dispatchers.Main) {
                        // textView.text = data.content[0].message
                        Log.d("DATA", data.content.toString())
                        //FRONT_END Populate the recyclerview
                        adapter.setPostData(data.content)
                    }
                }

            }
            //BACK_END: Handling call errors
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "no internet", Toast.LENGTH_LONG).show()
                    Log.d("ERROR", e.toString())
                }
            }

        }
        Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show()

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