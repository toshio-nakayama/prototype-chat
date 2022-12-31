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
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivityAddFriendBinding
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.NodeNames
import com.portfolio.prototype_chat.viewmodels.AddFriendViewModel
import com.portfolio.prototype_chat.views.util.setImage

class AddFriendActivity : AppCompatActivity() {
    
    private lateinit var talkRef: DatabaseReference
    private lateinit var binding: ActivityAddFriendBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        talkRef = Firebase.database.reference.child(NodeNames.TALK)
        val guestId = intent.getStringExtra(Extras.USER_ID)!!
        val viewModelFactory = AddFriendViewModel.Factory(guestId)
        val viewModel = ViewModelProvider(this, viewModelFactory)[AddFriendViewModel::class.java]
        viewModel.userLiveData.observe(this) {
            binding.textName.text = it.name
            setImage(applicationContext, it.photo, R.drawable.default_profile, binding.circularimageProfile)
        }
        binding.imagebuttonClose.setOnClickListener { finish() }
        binding.imagebuttonAdd.setOnClickListener { addFriend(guestId) }
    }
    
    
    private fun addFriend(guestId: String) {
        val hostId = Firebase.auth.currentUser?.uid ?: return
        val childUpdates = hashMapOf<String, Any>("/$hostId/$guestId/${NodeNames.TIME_STAMP}" to ServerValue.TIMESTAMP,
            "/$guestId/$hostId/${NodeNames.TIME_STAMP}" to ServerValue.TIMESTAMP)
        talkRef.updateChildren(childUpdates).addOnSuccessListener {
            val intent = Intent(this@AddFriendActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
}
