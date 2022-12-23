package com.portfolio.prototype_chat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivityLoginBinding
import com.portfolio.prototype_chat.utils.ToastGenerator
import com.portfolio.prototype_chat.utils.connectionAvailable

class LoginActivity : AppCompatActivity() {
    
    private val validation: AwesomeValidation = AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT)
    private lateinit var binding: ActivityLoginBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    
    override fun onStart() {
        super.onStart()
        val currentUser = Firebase.auth.currentUser
        currentUser?.run {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    private fun init() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.buttonLogin.setOnClickListener {
            if (connectionAvailable(applicationContext)) {
                if (validation.validate()) {
                    login()
                }
            } else {
                ToastGenerator.Builder(applicationContext).resId(R.string.offline).build()
            }
        }
        binding.buttonSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
        addValidationToViews()
    }
    
    private fun addValidationToViews() {
        validation.also { v ->
            v.addValidation(this,
                R.id.textinput_email,
                Patterns.EMAIL_ADDRESS,
                R.string.invalid_email)
            v.addValidation(this,
                R.id.textinput_email,
                RegexTemplate.NOT_EMPTY,
                R.string.empty_email)
            v.addValidation(this,
                R.id.textinput_password,
                RegexTemplate.NOT_EMPTY,
                R.string.empty_password)
        }
    }
    
    private fun login() {
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                ToastGenerator.Builder(applicationContext).resId(R.string.login_failure).build()
            }
    }
    
}