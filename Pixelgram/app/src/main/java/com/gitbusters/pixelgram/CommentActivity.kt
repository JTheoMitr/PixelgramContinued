package com.gitbusters.pixelgram

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gitbusters.pixelgram.api.Author
import com.gitbusters.pixelgram.api.PostComments
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import com.gitbusters.pixelgram.databinding.ActivityCommentBinding
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class CommentActivity : AppCompatActivity() {
    private val model : MainViewModel by viewModels()

    private var _binding: ActivityCommentBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Comments"
        val postid = intent.getIntExtra("postid", 1)
        getPostComments(postid)
        binding.btnSubmitComment.setOnClickListener {
            Log.d("Comment Fire","Message1")
            MainScope().launch {
                Log.d("Comment Fire","Message2")
                postComment()
            }
        }

    }

    private fun getPostComments(postid: Int) {
        val rview = findViewById<RecyclerView>(R.id.comment_recyclerview)
        rview.layoutManager = LinearLayoutManager(this@CommentActivity)

        model.returnComments(postid,0,15).observe(this,{
                comments -> comments.let{rview.adapter=CommentRecyclerAdapter(comments)}
        })

    }

    private suspend fun postComment() {

        // val accessToken = read("access_token")  //dont have lol
        class AuthInterceptor(context: Context) : Interceptor {
            // private val sessionManager = SessionManager(context)
            override fun intercept(chain: Interceptor.Chain): Response {
                val requestBuilder = chain.request().newBuilder()

                // If token has been saved, add it to the request
                requestBuilder.addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxa2MxRFV6UkhGczFlaXBfZ0daMG5QWHRTVkJCT3Y4Wjl5VVhsdVM4Y3owIn0.eyJleHAiOjE2NTEyNTU3NzcsImlhdCI6MTY1MTI1Mzk3NywianRpIjoiZWQ3YmNmOTUtMjk2MC00MGQ4LThhZGMtOTg2NWYxNjUzMmMwIiwiaXNzIjoiaHR0cHM6Ly9lbmFibGVtZW50LWtleWNsb2FrLndvcmsuY29nbml6YW50LnN0dWRpby9hdXRoL3JlYWxtcy9QaXhlbGdyYW0tTW9ub2xpdGgiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZjQzYzE1OTgtOGQ4ZS00ODRiLTg1MDAtMTQ2ZWJlZmQ5MDRmIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicGl4ZWxncmFtLW1vbm9saXRoLWJhY2tlbmQiLCJzZXNzaW9uX3N0YXRlIjoiY2YzZWRhZjUtYzg3Mi00NDRlLTk0ZGQtMGM4YzIyOTZkY2JjIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1waXhlbGdyYW0tbW9ub2xpdGgtYmFja2VuZCJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicHJlZmVycmVkX3VzZXJuYW1lIjoiMTIzIn0.HYQ4wEIz-K0DF_IIkwkFq2wfO5w9ym5Il8yhVNizLCs3DQOSQhQdg4X8QzWJrNMGlPzQnDWDoKpa33_1LcgT4thZxzp2vEbIe_iiWcb4RB-oqzzmEKbHXqClcpzq-T83jT7s_GKB5KUguNhu-nteRmgWLaYCSrcxxQjHw6RxX6OxGNXfFb0futjdio5COau0_-uEuf9NHIiKmRWhhTPfxOSh5xqKOTWPh6dtOOkRKMmKB6pclbdXxWe1gtp1xRvrNVZ1us5PUdrmZlmdG9FNM5eQigyeHn4VrUgteLwfSfC-z2PVnkc5ZTTC6axkwBailpb3ku1ftkb501hSsgI15w")
                return chain.proceed(requestBuilder.build())
            }

        }

        fun okhttpClient(context: Context): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .build()
        }



        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttpClient(this@CommentActivity))
            .build()
            .create(ApiInterface::class.java)

        // Log.d("ResponsePOSTComment", "outside response")
        MainScope().launch(Dispatchers.IO) {
            try{
                Log.d("ResponsePOSTComment", "Before response")
                val postComments = PostComments(Author(0,"",""),"",12,"ghost comment",12)




                val response = api.postComments(12,postComments).awaitResponse()
                Log.d("ResponsePOSTComment", "after response")
                Log.d("ResponsePOSTComment", response.toString())
                if (response.isSuccessful) {
                    val data = response.body()!!

                    withContext(Dispatchers.Main){
                        Log.d("ResponsePOSTComment", data.toString())
                        Log.d("ResponsePOSTComment", "We've grabbed the data")
                    }
                }
            }

            //BACK_END: Handling call errors
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("Post Comment Fail", e.toString())
                    Toast.makeText(applicationContext, "LOGOUT ERROR", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    private suspend fun read(key: String): String? {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }
}