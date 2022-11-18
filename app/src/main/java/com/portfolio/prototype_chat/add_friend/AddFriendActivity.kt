package com.portfolio.prototype_chat.add_friend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
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

class AddFriendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFriendBinding
    private lateinit var currentUser: FirebaseUser
    private lateinit var dbRootRef: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var dbRefTalk: DatabaseReference
    private lateinit var storageRootRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = Firebase.auth.currentUser!!
        dbRootRef = Firebase.database.reference
        dbRefUser = dbRootRef.child(NodeNames.USERS)
        dbRefTalk = dbRootRef.child(NodeNames.TALK)
        storageRootRef = Firebase.storage.reference
        val friendId = intent.getStringExtra(Extras.USER_ID)!!
        setProfile(friendId)
        binding.imageButtonClose.setOnClickListener { finish() }
        binding.imageButtonAdd.setOnClickListener {
            addFriend(friendId)
        }
    }

    private fun setProfile(friendId: String) {
        dbRefUser.child(friendId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val photoName = friendId + Constants.EXT_JPG
                val fileRef = storageRootRef.child(Constants.IMAGES_FOLDER).child(NodeNames.PHOTO)
                    .child(photoName)
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    binding.textViewName.text = snapshot.child(NodeNames.NAME).value.toString()
                    Glide.with(applicationContext)
                        .load(uri)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(binding.imageViewProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun addFriend(friendId: String) {
        val userId = currentUser.uid
        dbRefTalk.child(userId).child(friendId).child(NodeNames.TIME_STAMP)
            .setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
                dbRefTalk.child(friendId).child(userId).child(NodeNames.TIME_STAMP)
                    .setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
                        startActivity(Intent(this@AddFriendActivity, MainActivity::class.java))
                        finish()
                    }
            }
    }
}
