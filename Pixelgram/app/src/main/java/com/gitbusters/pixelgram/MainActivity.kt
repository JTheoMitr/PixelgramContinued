package com.gitbusters.pixelgram

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

const val BASE_URL = "http://34.134.148.105/"

class MainActivity : AppCompatActivity() {
    //BACK_END: Disabled Back button on landing page
    override fun onBackPressed() {}


    //lateinit for dataStore
    private lateinit var dataStore: DataStore<Preferences>
    //BACK_END: Added coroutine scope to project:
    override fun onCreate(savedInstanceState: Bundle?) = runBlocking {
        // Display the logo of the application
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.drawable.ic_pixelgram_logo)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setTitle(" Pixelgram")
        dataStore = createDataStore(name = "settings")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCurrentData()
        setLogo()
        logOutUser()


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

    private fun logOutUser() {

        //BACK_END: Building our retrofit Builder instance
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        MainScope().launch(Dispatchers.IO) {
            try {

                val response = api.logOut("eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhODcxNmY2NC1iNWFlLTQ3NjctYTViMS0zYmZiMzg1ZGRkNjYifQ.eyJleHAiOjE2NTA1NzY2NTIsImlhdCI6MTY1MDU3NDg1MiwianRpIjoiZjcwYjc1MmQtZjdkNy00ZDdlLThmMjYtNWQwODE0NjhkM2MyIiwiaXNzIjoiaHR0cHM6Ly9lbmFibGVtZW50LWtleWNsb2FrLndvcmsuY29nbml6YW50LnN0dWRpby9hdXRoL3JlYWxtcy9QaXhlbGdyYW0tTW9ub2xpdGgiLCJhdWQiOiJodHRwczovL2VuYWJsZW1lbnQta2V5Y2xvYWsud29yay5jb2duaXphbnQuc3R1ZGlvL2F1dGgvcmVhbG1zL1BpeGVsZ3JhbS1Nb25vbGl0aCIsInN1YiI6ImI3ZGFhMmMxLTRiNmYtNDc4Ni1iNWFlLTkxZTc4NmJhMjdiNCIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJwaXhlbGdyYW0tbW9ub2xpdGgtYmFja2VuZCIsInNlc3Npb25fc3RhdGUiOiJhZjQ1MGNmOC0yODQxLTRlMWUtOWY1Zi01ODBhYjZjNTRiODIiLCJzY29wZSI6ImVtYWlsIHByb2ZpbGUifQ.zdyT38EQ5FXnDe3gQmMq3dQbbzJb36kbyjA4hmYbP1o").awaitResponse()
                if (response.isSuccessful) {

                    Log.d("ResponseTest", "Call is Successful")
                    val data = response.body()!!
                    Log.d("ResponseTestData", data.toString())
                    Log.d("ResponseTestTwo", "We've grabbed the data")

                    withContext(Dispatchers.Main) {

                        Toast.makeText(applicationContext, "LOGOUT SUCCESS", Toast.LENGTH_LONG).show()

                    }
                }

            }
            //BACK_END: Handling call errors
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("LogOutFail", e.toString())
                    Toast.makeText(applicationContext, "LOGOUT ERROR", Toast.LENGTH_LONG).show()
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