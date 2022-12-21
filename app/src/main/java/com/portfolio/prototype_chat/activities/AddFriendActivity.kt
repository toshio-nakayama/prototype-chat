package com.portfolio.prototype_chat.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivityAddFriendBinding
import com.portfolio.prototype_chat.models.db.User
import com.portfolio.prototype_chat.utils.Constants
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.NodeNames
import com.portfolio.prototype_chat.utils.glideSupport
import com.portfolio.prototype_chat.viewmodels.AddFriendViewModel

class AddFriendActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddFriendBinding
    private lateinit var rootRef: DatabaseReference
    private lateinit var talkRef: DatabaseReference
    private lateinit var storageRootRef: StorageReference
    private lateinit var viewModel: AddFriendViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        rootRef = Firebase.database.reference
        talkRef = rootRef.child(NodeNames.TALK)
        storageRootRef = Firebase.storage.reference
        val friendId = intent.getStringExtra(Extras.USER_ID)!!
        
        val viewModelFactory = AddFriendViewModel.Factory(friendId)
        viewModel = ViewModelProvider(this, viewModelFactory)[AddFriendViewModel::class.java]
        viewModel.userLiveData.observe(this) { setProfile(friendId, it) }
        binding.imagebuttonClose.setOnClickListener { finish() }
        binding.imagebuttonAdd.setOnClickListener { addFriend(friendId) }
    }
    
    private fun setProfile(userId: String, user: User) {
        binding.textName.text = user.name
        val photoName = userId + Constants.EXT_JPG
        val fileRef = storageRootRef.child(Constants.IMAGES).child(NodeNames.PHOTO).child(photoName)
        fileRef.downloadUrl.addOnSuccessListener {
            glideSupport(applicationContext,
                it,
                R.drawable.default_profile,
                binding.circularimageProfile)
        }
    }
    
    private fun addFriend(friendId: String) {
        val currentUser = Firebase.auth.currentUser
        currentUser ?: return
        val userId = currentUser.uid
        talkRef.child(userId).child(friendId).child(NodeNames.TIME_STAMP)
            .setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
                talkRef.child(friendId).child(userId).child(NodeNames.TIME_STAMP)
                    .setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
                        val intent = Intent(this@AddFriendActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
            }
        
    }
    
}
