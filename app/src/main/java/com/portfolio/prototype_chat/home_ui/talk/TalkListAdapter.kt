package com.portfolio.prototype_chat.home_ui.talk

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.common.Constants
import com.portfolio.prototype_chat.common.Extras
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.TalkListLayoutBinding
import com.portfolio.prototype_chat.home_ui.talk.messaging.TalkActivity
import com.portfolio.prototype_chat.util.glideSupport

class TalkListAdapter(val context: Context) :
    ListAdapter<Talk, TalkListAdapter.ViewHolder>(DIFF_CALLBACK) {

    private val storageRootRef: StorageReference = Firebase.storage.reference

    inner class ViewHolder(val binding: TalkListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(item: Talk) {
            binding.apply {
                storageRootRef.child(Constants.IMAGES_FOLDER).child(NodeNames.PHOTO_URI_PATH)
                    .child(item.photoName)
                    .downloadUrl.addOnSuccessListener {
                        glideSupport(context, it, R.drawable.default_profile, imageViewProfile)
                    }
                textViewName.text = item.userName
                if (item.unreadCount != "0") {
                    relativeLayoutUnread.visibility = View.VISIBLE
                    textViewUnreadCount.text = item.unreadCount
                } else {
                    relativeLayoutUnread.visibility = View.GONE
                }
                linearLayoutTalkList.setOnClickListener {
                    val intent = Intent(context, TalkActivity::class.java)
                    intent.putExtra(Extras.USER_ID, item.userId)
                    intent.putExtra(Extras.USER_NAME, item.userName)
                    context.startActivity(intent)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TalkListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Talk>() {
            override fun areItemsTheSame(oldItem: Talk, newItem: Talk): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: Talk, newItem: Talk): Boolean {
                return oldItem == newItem
            }
        }
    }
}