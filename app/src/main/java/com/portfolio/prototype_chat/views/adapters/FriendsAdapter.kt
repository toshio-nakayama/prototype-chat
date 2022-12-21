package com.portfolio.prototype_chat.views.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.activities.FriendProfileActivity
import com.portfolio.prototype_chat.utils.Constants
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.NodeNames
import com.portfolio.prototype_chat.databinding.FriendsListLayoutBinding
import com.portfolio.prototype_chat.models.db.Friend
import com.portfolio.prototype_chat.utils.glideSupport

class FriendsAdapter(val context: Context) :
    ListAdapter<Friend, FriendsAdapter.ViewHolder>(DIFF_CALLBACK) {
    private val storageRootRef: StorageReference = Firebase.storage.reference

    inner class ViewHolder(val binding: FriendsListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(item: Friend) {
            binding.apply {
                storageRootRef.child(Constants.IMAGES).child(NodeNames.PHOTO)
                    .child(item.photoName).downloadUrl.addOnSuccessListener {
                        glideSupport(context, it, R.drawable.default_profile, circularimageProfile)
                    }
                textName.text = item.name
                textStatusmessage.text = item.statusMessage
                linearRow.setOnClickListener {
                    val intent = Intent(context, FriendProfileActivity::class.java).apply {
                        putExtra(Extras.USER_ID, item.id)
                        putExtra(Extras.USER_NAME, item.name)
                        putExtra(Extras.STATUS_MESSAGE, item.statusMessage)
                        putExtra(Extras.PHOTO_NAME, item.photoName)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FriendsListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(getItem(position))
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