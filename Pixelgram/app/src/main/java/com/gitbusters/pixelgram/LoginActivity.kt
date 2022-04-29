package com.gitbusters.pixelgram

import android.content.ContentValues
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import com.gitbusters.pixelgram.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataStore: DataStore<Preferences>


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usernameET : EditText = findViewById(R.id.editText_username)
        val passwordET : EditText = findViewById(R.id.editText_password)

        //BACK_END: Network was never reached, default case
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch {
                Toast.makeText(this@LoginActivity, "Device is Offline", Toast.LENGTH_LONG).show()
                Log.d(ContentValues.TAG,"Its not available")
                if((Settings.System.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1)) {
                    Log.d("Airplane Mode", "Airplane mode is on")
                    Toast.makeText(this@LoginActivity, "Airplane Mode is on", Toast.LENGTH_LONG).show()
                }}}

        binding.btnRegister.setOnClickListener {
            lifecycleScope.launch {
                Toast.makeText(this@LoginActivity, "Device is Offline", Toast.LENGTH_LONG).show()
                Log.d(ContentValues.TAG,"Its not available")
                if((Settings.System.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1)) {
                    Log.d("Airplane Mode", "Airplane mode is on")
                    Toast.makeText(this@LoginActivity, "Airplane Mode is on", Toast.LENGTH_LONG).show()
                }}}



        //BACK_END: Network Request Builder
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) //Indicates that this network should be able to reach the internet.
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI) //Indicates this network uses a Wi-Fi transport.
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR) //Indicates this network uses a cellular transport.(costly)
            .build()


        val networkCallback = object : ConnectivityManager.NetworkCallback() {

        //BACK_END: Network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d(ContentValues.TAG,"Its available!")
                binding.btnLogin.setOnClickListener {

                    val username = usernameET.text.toString()
                    val password = passwordET.text.toString()

                    fun checkIfEmptyInput(s: String, editText: EditText) {
                        if (s == "") {
                            editText.setError("Please Complete All Fields")
                            editText.setBackgroundResource(R.drawable.red_outline)
                            Toast.makeText(applicationContext, "Please Complete All Fields", Toast.LENGTH_LONG).show()
                        } else if (s != "") {
                            editText.setBackgroundResource(R.drawable.filled_border)
                            if (username != "" && password != "") {
                                lifecycleScope.launch {
                                    dataStore = createDataStore(name = "settings")
                                    getTokenData()
                                }
                            } else {
                                Toast.makeText(applicationContext, "Please Complete All Fields", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    checkIfEmptyInput(username, editText_username)
                    checkIfEmptyInput(password, editText_password)

                }

            binding.btnRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, UserRegister::class.java)
                startActivity(intent)
                lifecycleScope.launch {
                    dataStore = createDataStore(name = "settings")
                    registerUser()
                }

            }
            }

        //BACK_END: Network capabilities have changed and logged
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                Log.d(ContentValues.TAG,"change")
                Log.d("Unmetered",unmetered.toString())

            }


        //BACK_END: Lost Network Connection, Toast and check for airplane mode
            override fun onLost(network: Network) {
                super.onLost(network)
                binding.btnLogin.setOnClickListener {
                    lifecycleScope.launch {
                        Toast.makeText(this@LoginActivity, "No Internet Connection", Toast.LENGTH_LONG).show()
                        Log.d(ContentValues.TAG,"Its not available")
                        if((Settings.System.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1)) {
                            Log.d("Airplane Mode", "Airplane mode is on")
                            Toast.makeText(this@LoginActivity, "Airplane Mode is on", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        //BACK_END: ConnectivityManger making network request
        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)

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

        //BACK_END:  Call getTokenData from API and log refresh token
                val response = api.getTokenData(binding.editTextUsername.text.toString(), binding.editTextPassword.text.toString()).awaitResponse()
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
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }




            }
        //BACK_END: Handling call errors
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "No Internet Connected", Toast.LENGTH_LONG).show()
                    Log.d("TOKEN_ERROR", e.message.toString())
                }
            }
        }
        // BACK_END: This checks to make sure data was correctly wiped from most recent session.  Should be NULL
        lifecycleScope.launch {
            val value = read("refresh_token")
            Log.d("UserTokenCache", value.toString())
        }
    }

    private fun registerUser() {

        //BACK_END: Building our retrofit Builder instance
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        MainScope().launch(Dispatchers.IO) {
            try {
                Log.d("LAUNCH", "Register Call Attempted")

                //BACK_END:  Call getTokenData from API and log refresh token
                val response = api.registerUser(binding.editTextUsername.text.toString(), binding.editTextPassword.text.toString()).awaitResponse()
                Log.d("RESPONSE_CODE", response.code().toString())
                val responseCode = response.code().toString()
                if (responseCode == "500") {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            applicationContext,
                            "Account already exists",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                if (response.isSuccessful) {

                        val data = response.body()!!
                        withContext(Dispatchers.Main) {
                            Log.d("TOKEN", data.refresh_token)
                        }
                        lifecycleScope.launch {

                            save(
                                "refresh_token",
                                data.refresh_token
                            )

                        }
                        //BACK_END: navigate to main activity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }





            }
            //BACK_END: Handling call errors
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "We hit the catch", Toast.LENGTH_LONG).show()
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