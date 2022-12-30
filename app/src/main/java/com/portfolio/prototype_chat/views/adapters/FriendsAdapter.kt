package com.portfolio.prototype_chat.views.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.activities.FriendProfileActivity
import com.portfolio.prototype_chat.databinding.FriendsListLayoutBinding
import com.portfolio.prototype_chat.models.db.Friend
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.glideSupport

class FriendsAdapter(val context: Context) :
    ListAdapter<Friend, FriendsAdapter.ViewHolder>(DIFF_CALLBACK) {
    
    inner class ViewHolder(val binding: FriendsListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: Friend) {
            binding.textName.text = item.name
            binding.textStatusmessage.text = item.statusMessage
            item.photo?.let {
                Firebase.storage.getReferenceFromUrl(item.photo)
                    .downloadUrl.addOnSuccessListener {
                        glideSupport(context, it, R.drawable.default_profile,
                            binding.circularimageProfile)
                    }
            }
            binding.linearRow.setOnClickListener {
                val intent = Intent(context, FriendProfileActivity::class.java).apply {
                    putExtra(Extras.USER_ID, item.id)
                    putExtra(Extras.USER_NAME, item.name)
                    putExtra(Extras.STATUS_MESSAGE, item.statusMessage)
                    putExtra(Extras.PHOTO_NAME, item.photo)
                }
                context.startActivity(intent)
            }
            
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FriendsListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    override fun submitList(list: List<Friend>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
    
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Friend>() {
            override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
                return oldItem.id == newItem.id
            }
            
            override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
                return oldItem == newItem
            }
            
        }
    }
    
}