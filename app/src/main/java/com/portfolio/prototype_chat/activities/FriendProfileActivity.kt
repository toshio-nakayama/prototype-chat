package com.portfolio.prototype_chat.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivityFriendProfileBinding
import com.portfolio.prototype_chat.fragments.MessageDisplayFragment
import com.portfolio.prototype_chat.models.db.Friend
import com.portfolio.prototype_chat.utils.Constants
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.NodeNames
import com.portfolio.prototype_chat.utils.glideSupport

class FriendProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendProfileBinding
    private lateinit var rootRef: DatabaseReference
    private lateinit var storageRootRef: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rootRef = Firebase.database.reference
        storageRootRef = Firebase.storage.reference

        val friendId = intent.getStringExtra(Extras.USER_ID)!!
        val friendName = intent.getStringExtra(Extras.USER_NAME)!!
        val friendStatusMessage = intent.getStringExtra(Extras.STATUS_MESSAGE)!!
        val friendPhotoName = intent.getStringExtra(Extras.PHOTO_NAME)!!
        setProfile(Friend(friendId, friendName, friendStatusMessage, friendPhotoName))
        binding.textStatusmessage.setOnClickListener {
            if (binding.textStatusmessage.length() > 0) {
                val message = binding.textStatusmessage.text.toString()
                val dialogFragment = MessageDisplayFragment.newInstance(message)
                dialogFragment.show(supportFragmentManager, MessageDisplayFragment.TAG_DIALOG)
            }
        }
        binding.imagebuttonTalk.setOnClickListener {
            val intent =
                Intent(this@FriendProfileActivity, MessagesActivity::class.java).apply {
                    putExtra(Extras.USER_ID, friendId)
                    putExtra(Extras.USER_NAME, friendName)
                    putExtra(Extras.PHOTO_NAME, friendPhotoName)
                }
            startActivity(intent)
        }
    }

    private fun setProfile(friend: Friend) {
        binding.textName.text = friend.name
        binding.textStatusmessage.text = friend.statusMessage
        storageRootRef.child(Constants.IMAGES).child(NodeNames.PHOTO)
            .child(friend.photoName).downloadUrl.addOnSuccessListener {
                glideSupport(applicationContext,
                    it,
                    R.drawable.default_profile,
                    binding.circularimageProfile)
            }
        storageRootRef.child(Constants.IMAGES).child(NodeNames.BACKGROUND_PHOTO)
            .child(friend.photoName).downloadUrl.addOnSuccessListener {
                glideSupport(applicationContext,
                    it,
                    R.drawable.default_background,
                    binding.imageBackground)
            }
    }
}