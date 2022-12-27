package com.portfolio.prototype_chat.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.portfolio.prototype_chat.utils.UpdateUI
import com.portfolio.prototype_chat.utils.glideSupport
import com.portfolio.prototype_chat.viewmodels.FriendProfileViewModel
import kotlinx.android.synthetic.main.activity_friend_profile.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.circularimage_profile
import kotlinx.android.synthetic.main.fragment_home.text_name
import kotlinx.android.synthetic.main.fragment_home.text_statusmessage
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.image_background

class FriendProfileActivity : AppCompatActivity() {
    
    private lateinit var storageRootRef: StorageReference
    private val handler:Handler = Handler(Looper.getMainLooper())
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
            UpdateUI(handler).apply {
                setTextAsync(text_name, user.name)
                setTextAsync(text_statusmessage, user.statusMessage)
                setImageAsync(applicationContext, user.photo, R.drawable.default_profile,
                    circularimage_profile)
                setImageAsync(applicationContext, user.backgroundPhoto, R.drawable
                    .default_background, image_background)
            }
            text_statusmessage.setOnClickListener {
                if (text_statusmessage.length() > 0) {
                    val message = text_statusmessage.text.toString()
                    val dialogFragment = MessageDisplayFragment.newInstance(message)
                    dialogFragment.show(supportFragmentManager, MessageDisplayFragment.TAG_DIALOG)
                }
            }
            imagebutton_talk.setOnClickListener {
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
    
}