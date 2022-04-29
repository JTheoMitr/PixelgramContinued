package com.gitbusters.pixelgram

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.gitbusters.pixelgram.api.Post
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint

const val BASE_URL = "http://34.134.148.105/"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val model : MainViewModel by viewModels()

    lateinit var layoutManager: LinearLayoutManager
    var adapter = PostRecyclerAdapter(listOf()) // listOf<Post>()
    var page = 0
    var loggedIn = false
        //BACK_END: Disabled Back button on landing page
    override fun onBackPressed() {}


    //lateinit for dataStore
    private lateinit var dataStore: DataStore<Preferences>
    //BACK_END: Added coroutine scope to project:
    override fun onCreate(savedInstanceState: Bundle?) {

        // Display the logo of the application
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_post_list)
       // recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        dataStore = createDataStore(name = "settings")

        updateCurrentData(page, adapter)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    // When the recyclerview gets to the bottom
                    page++ // Increment the page number.
                    updateCurrentData(page, adapter)
                    // DEBUG Toast.makeText(this@MainActivity, "$page", Toast.LENGTH_LONG).show()
                }
            }
        })
        setLogo()
        logOutUser()


    }

    //BACK_END: Method to build retrofit instance and create calls
    private fun updateCurrentData(pn : Int, adapter: PostRecyclerAdapter) {

        model.returnPosts(pn,30).observe(this,{
                posts -> posts.let{adapter.setPostData(posts.content)}
        })

        //BACK_END: Building our retrofit Builder instance
//        val api = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiInterface::class.java)
//
//        MainScope().launch(Dispatchers.IO) {
//            try {
//
//                val response = api.getPosts(pn, 5).awaitResponse()
//
//                if (response.isSuccessful) {
//                    val data = response.body()!!
//                    Log.d(TAG, data.content.toString())
//
//                    withContext(Dispatchers.Main) {
//                        // textView.text = data.content[0].message
//                        Log.d("DATA", data.content.toString())
//                        //FRONT_END Populate the recyclerview
//                        adapter.setPostData(data.content)
//                    }
//                }
//
//            }
//            //BACK_END: Handling call errors
//            catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(applicationContext, "no internet", Toast.LENGTH_LONG).show()
//                    Log.d("ERROR", e.toString())
//                }
//            }
//
//        }

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
                val userRefreshToken = read("refresh_token")
                Log.d("LogoutRefreshToken", userRefreshToken.toString())

                val response = api.logOut(userRefreshToken.toString()).awaitResponse()
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Skeleton functions for toolbar actions
            R.id.action_new_post -> {
                Toast.makeText(this, "New Post Button press", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_profile -> {

                val btnView = findViewById<View>(R.id.action_profile)
                val popup = PopupMenu(this, btnView)
                popup.inflate(R.menu.loginout_popup_menu)

                if (!loggedIn) {
                    popup.menu.findItem(R.id.menuLogin).setVisible(true)
                    popup.menu.findItem(R.id.menuLogout).setVisible(false)
                } else {
                    popup.menu.findItem(R.id.menuLogin).setVisible(false)
                    popup.menu.findItem(R.id.menuLogout).setVisible(true)
                }

                popup.setOnMenuItemClickListener {
                    val intent = Intent(this, LoginActivity::class.java)
                    when (it.itemId) {
                        R.id.menuLogout -> {
                            Toast.makeText(this, "Click Logout", Toast.LENGTH_SHORT).show()
                            loggedIn = false
                            startActivity(intent)
                        }
                        R.id.menuLogin -> {
                            Toast.makeText(this, "Click Login", Toast.LENGTH_SHORT).show()
                            loggedIn = true
                            startActivity(intent)

                        }
                    }
                    true
                }
                popup.show()
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
        /*Changes login and logout button on toolbar*/
    }

    /* Set the app logo on the toolbar */
    fun setLogo() {
        val toolbarTitle = " Pixelgram"
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.drawable.ic_pixelgram_logo)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = toolbarTitle
    }
    private suspend fun read(key: String): String? {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }
}