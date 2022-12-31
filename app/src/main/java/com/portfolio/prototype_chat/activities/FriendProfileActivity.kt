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
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.guard
import com.portfolio.prototype_chat.viewmodels.FriendProfileViewModel
import com.portfolio.prototype_chat.views.util.setImage

class FriendProfileActivity : AppCompatActivity() {
    
    private lateinit var storageRootRef: StorageReference
    private lateinit var binding: ActivityFriendProfileBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupActionBar()
        storageRootRef = Firebase.storage.reference
        val guestId = intent.getStringExtra(Extras.USER_ID)!!
        val viewModelFactory = FriendProfileViewModel.Factory(guestId)
        val viewModel =
            ViewModelProvider(this, viewModelFactory)[FriendProfileViewModel::class.java]
        viewModel.userLiveData.observe(this) { user ->
            binding.textName.text = user.name
            binding.textStatusmessage.text = user.statusMessage
            setImage(applicationContext, user.photo, R.drawable.default_profile,
                binding.circularimageProfile)
            setImage(applicationContext, user.backgroundPhoto, R.drawable
                .default_background, binding.imageBackground)
            
            binding.textStatusmessage.setOnClickListener { showDialog() }
            binding.imagebuttonTalk.setOnClickListener {
                launchActivity(guestId, user.name, user.photo)
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
    
    private fun launchActivity(id: String, userName: String?, photoName: String?) {
        guard(userName, photoName) { uName, pName ->
            {
                val intent =
                    Intent(this@FriendProfileActivity, MessagesActivity::class.java).apply {
                        putExtra(Extras.USER_ID, id)
                        putExtra(Extras.USER_NAME, uName)
                        putExtra(Extras.PHOTO_NAME, pName)
                    }
                startActivity(intent)
            }
        }
        
    }
    
    private fun showDialog() {
        if (binding.textStatusmessage.length() > 0) {
            val message = binding.textStatusmessage.text.toString()
            val dialogFragment = MessageDisplayFragment.newInstance(message)
            dialogFragment.show(supportFragmentManager, MessageDisplayFragment.TAG_DIALOG)
        }
    }
    
    private fun setupActionBar() {
        supportActionBar?.run {
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }
}