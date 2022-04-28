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
import kotlinx.android.synthetic.main.activity_user_register.*

class UserRegister : AppCompatActivity() {
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
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
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
}