package com.gitbusters.pixelgram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_user_register.*
import kotlinx.android.synthetic.main.post_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class UserRegister : AppCompatActivity() {

    private lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)


        val submit: Button = findViewById(R.id.submit_button)
        val passwordVar: EditText = findViewById(R.id.editTextPassword)
        val password2Var: EditText = findViewById(R.id.editTextPassword2)
        val usernameVar : EditText = findViewById(R.id.editTextUsername)



        submit.setOnClickListener {
            val password = passwordVar.text.toString()
            val password2 = password2Var.text.toString()
            val username = usernameVar.text.toString()

            fun checkPasswords() {
                if (username != "" && password != "" && password2 != "") {
                    if (password == password2) {
                        dataStore = createDataStore(name = "settings")
                        registerUser()

                    } else {
                        Log.d("INFO", "Test")
                        Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Please Complete All Fields", Toast.LENGTH_LONG).show()
                }
            }

            fun checkIfEmpty(s: String, editText: EditText) {
                if (s == "") {
                    editText.setError("Please Complete All Fields")
                    editText.setBackgroundResource(R.drawable.red_outline)
                    Toast.makeText(applicationContext, "Please Complete All Fields", Toast.LENGTH_LONG).show()
                } else if (s != "") {
                    editText.setBackgroundResource(R.drawable.filled_border)
                    checkPasswords()
                }
            }
            checkIfEmpty(username, editTextUsername)
            checkIfEmpty(password, editTextPassword)
            checkIfEmpty(password2, editTextPassword2)
        }

        fun checkIfEmpty(s: String, editText: EditText) {
            if (s == "") {
                editText.setError("Please Complete All Fields")
                editText.setBackgroundResource(R.drawable.red_outline)
            }
        }


        showHideBtn.setOnClickListener {

            if (showHideBtn.text.toString().equals("SHOW")) {
                editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                editTextPassword2.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showHideBtn.text = getString(R.string.hide)
                showHideBtn2.text = getString(R.string.hide)
            } else {
                editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                editTextPassword2.transformationMethod = PasswordTransformationMethod.getInstance()
                showHideBtn.text = getString(R.string.show)
                showHideBtn2.text = getString(R.string.show)
            }
        }
        showHideBtn2.setOnClickListener {
            if (showHideBtn.text.toString().equals("SHOW")) {
                editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                editTextPassword2.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showHideBtn.text = getString(R.string.hide)
                showHideBtn2.text = getString(R.string.hide)
            } else {
                editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                editTextPassword2.transformationMethod = PasswordTransformationMethod.getInstance()
                showHideBtn.text = getString(R.string.show)
                showHideBtn2.text = getString(R.string.show)
            }
        }

    }

    private fun registerUser() {

        val usernameVar : EditText = findViewById(R.id.editTextUsername)
        val password2Var: EditText = findViewById(R.id.editTextPassword2)

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
                val response = api.registerUser(usernameVar.text.toString(), password2Var.text.toString()).awaitResponse()
                Log.d("RESPONSE_CODE", response.code().toString())
                val responseCode = response.code().toString()
                if (responseCode == "500") {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            applicationContext,
                            "Account Has Been Registered",
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
                    val intent = Intent(this@UserRegister, MainActivity::class.java)
                    startActivity(intent)
                }





            }
            //BACK_END: Handling call errors
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Please try again", Toast.LENGTH_LONG).show()
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
}