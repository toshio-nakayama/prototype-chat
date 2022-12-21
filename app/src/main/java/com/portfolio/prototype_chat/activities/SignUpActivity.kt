package com.portfolio.prototype_chat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivitySignupBinding
import com.portfolio.prototype_chat.models.db.User
import com.portfolio.prototype_chat.utils.NodeNames
import com.portfolio.prototype_chat.utils.ToastGenerator
import com.portfolio.prototype_chat.utils.connectionAvailable

class SignUpActivity : AppCompatActivity() {
    private lateinit var validation: AwesomeValidation
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference
        validation = AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT)
        initView()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        currentUser?.let { reload() }
    }

    private fun initView() {
        supportActionBar?.hide()
        binding.buttonSubmit.setOnClickListener {
            if (!connectionAvailable(applicationContext)) {
                ToastGenerator.Builder(applicationContext).resId(R.string.offline).build()
            } else if (validation.validate()) {
                val name = binding.editName.text.toString().trim()
                val email = binding.editEmail.text.toString().trim()
                val password = binding.editPassword.text.toString().trim()
                signUp(name, email, password)
            }
        }
        addValidationToViews()
    }

    private fun addValidationToViews() {
        validation.also { v ->
            v.addValidation(this, R.id.textinput_name, "[!-~]{1,20}", R.string.err_name)
            v.addValidation(this,
                R.id.textinput_name,
                RegexTemplate.NOT_EMPTY,
                R.string.err_name_blank)
            v.addValidation(this, R.id.textinput_email, Patterns.EMAIL_ADDRESS, R.string.err_email)
            v.addValidation(this,
                R.id.textinput_email,
                RegexTemplate.NOT_EMPTY,
                R.string.err_email_blank)
            val regexPassword =
                "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{6,20}"
            v.addValidation(this, R.id.textinput_password, regexPassword, R.string.err_password)
            v.addValidation(this,
                R.id.textinput_password,
                RegexTemplate.NOT_EMPTY,
                R.string.err_password_blank)
            v.addValidation(this,
                R.id.textinput_confirmpassword,
                R.id.textinput_password,
                R.string.err_confirm_password)
            v.addValidation(this,
                R.id.textinput_confirmpassword,
                R.id.textinput_password,
                R.string.err_confirm_password_blank)
        }
    }

    private fun signUp(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                currentUser = auth.currentUser!!
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                currentUser.updateProfile(profileUpdates)
                    .addOnSuccessListener {
                        writeNewUser(currentUser.uid, name, email)
                    }
            }
    }

    private fun writeNewUser(userId: String, name: String, email: String) {
        val user = User(name = name, email = email)
        database.child(NodeNames.USERS).child(userId).setValue(user).addOnSuccessListener {
            ToastGenerator.Builder(applicationContext).resId(R.string.signup_successfully).build()
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }
    }

    private fun reload() {
        finish()
        startActivity(intent)
    }
}