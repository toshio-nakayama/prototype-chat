package com.portfolio.prototype_chat.home_ui.friends

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.common.Constants
import com.portfolio.prototype_chat.common.Extras
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.ActivityFriendProfileBinding
import com.portfolio.prototype_chat.home_ui.talk.messaging.MessagesActivity
import com.portfolio.prototype_chat.profile.MessageDisplayFragment

class FriendProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendProfileBinding
    private lateinit var dbRootRef: DatabaseReference
    private lateinit var storageRootRef: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRootRef = Firebase.database.reference
        storageRootRef = Firebase.storage.reference

        val friendId = intent.getStringExtra(Extras.USER_ID)!!
        val friendName = intent.getStringExtra(Extras.USER_NAME)!!
        val friendStatusMessage = intent.getStringExtra(Extras.STATUS_MESSAGE)!!
        val friendPhotoName = intent.getStringExtra(Extras.PHOTO_NAME)!!
        setProfile(Friend(friendId, friendName, friendStatusMessage, friendPhotoName))
        binding.textStatusmessage.setOnClickListener {
            if (binding.textStatusmessage.length() > 0) {
                val dialogFragment = MessageDisplayFragment()
                val args = Bundle()
                val message = binding.textStatusmessage.text.toString()
                args.putString(Extras.MESSAGE, message)
                dialogFragment.arguments = args
                dialogFragment.show(supportFragmentManager, MessageDisplayFragment.DIALOG_TAG)
            }
        }
        binding.imagebuttonTalk.setOnClickListener {
            val intent = Intent(this@FriendProfileActivity, MessagesActivity::class.java)
            intent.apply {
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
            .child(friend.photoName).downloadUrl.addOnSuccessListener { uri ->
                Glide.with(applicationContext).load(uri).placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile).into(binding.circularimageProfile)
            }
        storageRootRef.child(Constants.IMAGES).child(NodeNames.BACKGROUND_PHOTO)
            .child(friend.photoName).downloadUrl.addOnSuccessListener { uri ->
                Glide.with(applicationContext).load(uri).placeholder(R.drawable.default_background)
                    .error(R.drawable.default_background).into(binding.imageBackground)
            }

    }
}