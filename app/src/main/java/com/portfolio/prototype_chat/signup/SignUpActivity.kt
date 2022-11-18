package com.portfolio.prototype_chat.signup

import android.content.Intent
import android.os.Bundle
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
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.ActivitySignupBinding
import com.portfolio.prototype_chat.login.LoginActivity
import com.portfolio.prototype_chat.util.ToastGenerator
import com.portfolio.prototype_chat.util.connectionAvailable

class SignUpActivity : AppCompatActivity() {
    private lateinit var validation: AwesomeValidation
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var currentUser: FirebaseUser? = null

    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        validation = AwesomeValidation(ValidationStyle.BASIC)
        validation.also { v ->
            v.addValidation(this, R.id.edit_text_name, RegexTemplate.NOT_EMPTY, R.string.empty_name)
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
            v.addValidation(
                this,
                R.id.confirmPasswordEditText,
                RegexTemplate.NOT_EMPTY,
                R.string.empty_confirm_password
            )
        }

        binding.submitButton.setOnClickListener {
            if (!connectionAvailable(applicationContext)) {
                ToastGenerator.Builder(applicationContext).resId(R.string.offline).build()
            } else if (validation.validate()) {
                val name = binding.editTextName.text.toString().trim()
                val email = binding.editTextEmail.text.toString().trim()
                val password = binding.editTextPassword.text.toString().trim()
                signUp(name, email, password)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        currentUser?.let { reload() }
    }

    private fun signUp(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                currentUser = auth.currentUser
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                currentUser!!.updateProfile(profileUpdates)
                    .addOnSuccessListener {
                        writeNewUser(currentUser!!.uid, name, email)
                    }
            }
    }

    private fun writeNewUser(userId: String, name: String, email: String) {
        val user = User(name = name, email = email)
        database.child(NodeNames.USERS).child(userId).setValue(user)
        ToastGenerator.Builder(applicationContext).resId(R.string.signup_successfully).build();
        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
    }

    private fun reload() {
        finish()
        startActivity(intent);
    }
}