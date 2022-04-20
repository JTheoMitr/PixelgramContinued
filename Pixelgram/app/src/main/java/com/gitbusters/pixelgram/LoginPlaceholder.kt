package com.gitbusters.pixelgram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import com.gitbusters.pixelgram.databinding.ActivityLoginPlaceholderBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class LoginPlaceholder : AppCompatActivity() {

    private var _binding: ActivityLoginPlaceholderBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginPlaceholderBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //login button
        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                dataStore = createDataStore(name = "settings")
                getTokenData()
                // will be data.refresh_token after we add getTokenData (not binding.etSaveKey)

            }
        }

    }

    //BACK_END: Method to build retrofit instance, retrieve current user's refresh token, and log in
    private fun getTokenData() {

        //BACK_END: Building our retrofit Builder instance
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        MainScope().launch(Dispatchers.IO) {
            try {
                Log.d("LAUNCH", "we are gonna try")

                //Call getTokenData from API and log refresh token
                val response = api.getTokenData("GitBusters","GitBustersPass").awaitResponse()
                Log.d("TOKEN_PRE", response.toString())
                if (response.isSuccessful) {
                    val data = response.body()!!
                    withContext(Dispatchers.Main) {
                        Log.d("TOKEN", data.refresh_token)
                    }
                    lifecycleScope.launch {

                        save("refresh_token",
                            data.refresh_token)

                    }
                    //BACK_END will eventually use this to navigate to main activity
                    val intent = Intent(this@LoginPlaceholder, MainActivity::class.java)
                    //startActivity(intent)
                }




            }
            //BACK_END: Handling call errors
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Call Error", Toast.LENGTH_LONG).show()
                    Log.d("TOKEN_ERROR", e.message.toString())
                }
            }
        }
        lifecycleScope.launch {
            val value = read("refresh_token")
            Log.d("UserToken", value.toString())
        }
    }

    //BACK_END: save and read methods for dataStore:
    private suspend fun save(key: String, value: String) {
        val dataStoreKey = preferencesKey<String>(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    private suspend fun read(key: String): String? {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}