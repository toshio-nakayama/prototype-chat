package com.portfolio.prototype_chat.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivityFriendProfileBinding
import com.portfolio.prototype_chat.fragments.MessageDisplayFragment
import com.portfolio.prototype_chat.models.db.User
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.glideSupport
import com.portfolio.prototype_chat.viewmodels.FriendProfileViewModel

class FriendProfileActivity : AppCompatActivity() {
    
    private lateinit var storageRootRef: StorageReference
    private lateinit var binding: ActivityFriendProfileBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.run {
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        storageRootRef = Firebase.storage.reference
        val guestId = intent.getStringExtra(Extras.USER_ID)!!
        val viewModelFactory = FriendProfileViewModel.Factory(guestId)
        val viewModel =
            ViewModelProvider(this, viewModelFactory)[FriendProfileViewModel::class.java]
        viewModel.userLiveData.observe(this) { user ->
            updateUI(user)
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
                        putExtra(Extras.USER_ID, guestId)
                        putExtra(Extras.USER_NAME, user.name)
                        putExtra(Extras.PHOTO_NAME, user.photo)
                    }
                startActivity(intent)
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
    
    private fun updateUI(user: User) {
        binding.textName.text = user.name
        binding.textStatusmessage.text = user.statusMessage
        user.photo?.let {
            Firebase.storage.getReferenceFromUrl(user.photo)
                .downloadUrl.addOnSuccessListener {
                    glideSupport(this,
                        it,
                        R.drawable.default_profile,
                        binding.circularimageProfile)
                }
        }
        user.backgroundPhoto?.let {
            Firebase.storage.getReferenceFromUrl(user.backgroundPhoto)
                .downloadUrl.addOnSuccessListener {
                    glideSupport(this,
                        it,
                        R.drawable.default_background,
                        binding.imageBackground)
                }
        }
    }
}