package com.portfolio.prototype_chat.home_ui.friends

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.common.Constants
import com.portfolio.prototype_chat.common.Extras
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.FriendsListLayoutBinding

class FriendsRVAdapter(val context: Context, private val allFriends: List<Friend>) :
    RecyclerView.Adapter<FriendsRVAdapter.ViewHolder>() {

    private lateinit var storageRootRef: StorageReference

    inner class ViewHolder(item: FriendsListLayoutBinding) : RecyclerView.ViewHolder(item.root) {
        val binding = item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        storageRootRef = Firebase.storage.reference
        val binding =
            FriendsListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFriend = allFriends[position]
        storageRootRef.child(Constants.IMAGES_FOLDER).child(NodeNames.PHOTO)
            .child(currentFriend.photoName).downloadUrl.addOnSuccessListener { uri ->
                Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(holder.binding.imageViewProfile)
            }
        holder.binding.textViewName.text = currentFriend.name
        holder.binding.textViewStatusMessage.text = currentFriend.statusMessage
        holder.binding.linearLayoutFriendsList.setOnClickListener{
            val intent = Intent(context, FriendProfileActivity::class.java)
            intent.apply {
                intent.putExtra(Extras.USER_ID, currentFriend.id)
                intent.putExtra(Extras.USER_NAME, currentFriend.name)
                intent.putExtra(Extras.STATUS_MESSAGE, currentFriend.statusMessage)
                intent.putExtra(Extras.PHOTO_NAME, currentFriend.photoName)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return allFriends.size
    }
}