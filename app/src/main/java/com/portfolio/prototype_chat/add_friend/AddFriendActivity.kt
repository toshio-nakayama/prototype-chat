package com.portfolio.prototype_chat.add_friend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.MainActivity
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.common.Constants
import com.portfolio.prototype_chat.common.Extras
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.ActivityAddFriendBinding
import com.portfolio.prototype_chat.signup.User

class AddFriendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFriendBinding
    private lateinit var currentUser: FirebaseUser
    private lateinit var rootRef: DatabaseReference
    private lateinit var talkRef: DatabaseReference
    private lateinit var storageRootRef: StorageReference
    private lateinit var viewModel: AddFriendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = Firebase.auth.currentUser!!
        rootRef = Firebase.database.reference
        talkRef = rootRef.child(NodeNames.TALK)
        storageRootRef = Firebase.storage.reference

        val friendId = intent.getStringExtra(Extras.USER_ID)!!
        val viewModelFactory = AddFriendViewModelFactory(friendId)
        viewModel = ViewModelProvider(this, viewModelFactory)[AddFriendViewModel::class.java]
        viewModel.user.observe(this) {
            setProfile(friendId, it)
        }
        binding.imageButtonClose.setOnClickListener { finish() }
        binding.imageButtonAdd.setOnClickListener {
            addFriend(friendId)
        }
    }

    private fun setProfile(userId: String, user: User) {
        binding.textViewName.text = user.name
        val photoName = userId + Constants.EXT_JPG
        val fileRef =
            storageRootRef.child(Constants.IMAGES_FOLDER).child(NodeNames.PHOTO_URI_PATH)
                .child(photoName)
        fileRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(applicationContext)
                .load(uri)
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .into(binding.imageViewProfile)
        }
    }

    private fun addFriend(friendId: String) {
        val userId = currentUser.uid
        talkRef.child(userId).child(friendId).child(NodeNames.TIME_STAMP)
            .setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
                talkRef.child(friendId).child(userId).child(NodeNames.TIME_STAMP)
                    .setValue(ServerValue.TIMESTAMP)
                    .addOnSuccessListener {
                        startActivity(Intent(this@AddFriendActivity, MainActivity::class.java))
                        finish()
                    }
            }
    }
}
