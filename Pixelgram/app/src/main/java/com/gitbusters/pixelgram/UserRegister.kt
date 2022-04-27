package com.gitbusters.pixelgram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class UserRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        val submit: Button = findViewById(R.id.submit_button)
        val passwordVar: EditText = findViewById(R.id.password)
        val password2Var: EditText = findViewById(R.id.password2)
        val usernameVar : EditText = findViewById(R.id.editTextUsername)



        submit.setOnClickListener {
            val password = passwordVar.text.toString()
            val password2 = password2Var.text.toString()
            val username = usernameVar.text.toString()

            if (username == "" || password == "" || password2 == "") {
                Toast.makeText(applicationContext, "Please Complete All Fields", Toast.LENGTH_LONG).show()
            } else {
                if (password == password2) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.d("INFO", "Test")
                    Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }
}