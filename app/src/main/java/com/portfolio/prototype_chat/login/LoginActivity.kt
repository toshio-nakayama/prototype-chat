package com.portfolio.prototype_chat.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.MainActivity
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivityLoginBinding
import com.portfolio.prototype_chat.signup.SignUpActivity
import com.portfolio.prototype_chat.util.ToastGenerator
import com.portfolio.prototype_chat.util.connectionAvailable

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var validation: AwesomeValidation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupValidation()
        auth = Firebase.auth
        binding.buttonLogin.setOnClickListener {
            if (!connectionAvailable(applicationContext)) {
                ToastGenerator.Builder(applicationContext).resId(R.string.offline).build()
            } else if (validation.validate()) {
                login()
            }
        }
        binding.textViewSignUp.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    SignUpActivity::class.java
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        currentUser?.run {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun login() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        MainActivity::class.java
                    )
                )
            }
            .addOnFailureListener {
                ToastGenerator.Builder(applicationContext).resId(R.string.login_failure).build()
            }
    }

    private fun setupValidation() {
        validation = AwesomeValidation(ValidationStyle.BASIC)
        validation.also { v ->
            v.addValidation(
                this,
                R.id.edit_text_email,
                Patterns.EMAIL_ADDRESS,
                R.string.invalid_email
            )
            v.addValidation(
                this,
                R.id.edit_text_email,
                RegexTemplate.NOT_EMPTY,
                R.string.empty_email
            )
            v.addValidation(
                this,
                R.id.edit_text_password,
                RegexTemplate.NOT_EMPTY,
                R.string.empty_password
            )
        }
    }
}