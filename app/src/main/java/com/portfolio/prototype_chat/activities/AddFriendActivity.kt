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
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivityAddFriendBinding
import com.portfolio.prototype_chat.models.db.User
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.NodeNames
import com.portfolio.prototype_chat.utils.glideSupport
import com.portfolio.prototype_chat.viewmodels.AddFriendViewModel

class AddFriendActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddFriendBinding
    private lateinit var rootRef: DatabaseReference
    private lateinit var talkRef: DatabaseReference
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        rootRef = Firebase.database.reference
        talkRef = rootRef.child(NodeNames.TALK)
        val guestId = intent.getStringExtra(Extras.USER_ID)!!
        
        val viewModelFactory = AddFriendViewModel.Factory(guestId)
        val viewModel = ViewModelProvider(this, viewModelFactory)[AddFriendViewModel::class.java]
        viewModel.userLiveData.observe(this) { updateUI(it) }
        binding.imagebuttonClose.setOnClickListener { finish() }
        binding.imagebuttonAdd.setOnClickListener { addFriend(guestId) }
    }
    
    private fun updateUI(user: User) {
        binding.textName.text = user.name
        Firebase.storage.getReferenceFromUrl(user.photo)
            .downloadUrl.addOnSuccessListener {
                glideSupport(applicationContext,
                    it,
                    R.drawable.default_profile,
                    binding.circularimageProfile)
            }
    }
    
    private fun addFriend(guestId: String) {
        val hostId = Firebase.auth.currentUser?.uid ?: return
        val childUpdates = hashMapOf<String, Any>(
            "/$hostId/$guestId/${NodeNames.TIME_STAMP}" to ServerValue.TIMESTAMP,
            "/$guestId/$hostId/${NodeNames.TIME_STAMP}" to ServerValue.TIMESTAMP
        )
        talkRef.updateChildren(childUpdates).addOnSuccessListener {
            val intent = Intent(this@AddFriendActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
}
