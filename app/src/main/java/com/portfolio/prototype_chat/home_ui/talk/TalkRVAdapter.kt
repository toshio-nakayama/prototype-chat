package com.portfolio.prototype_chat.home_ui.talk

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.common.Constants
import com.portfolio.prototype_chat.common.Extras
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.TalkListLayoutBinding

class TalkRVAdapter(val context: Context, private val allTalk: List<Talk>): RecyclerView.Adapter<TalkRVAdapter.ViewHolder>() {

    private lateinit var storageRootRef:StorageReference

    inner class ViewHolder(item:TalkListLayoutBinding):RecyclerView.ViewHolder(item.root) {
        val binding = item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TalkListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val talk = allTalk[position]
        storageRootRef = Firebase.storage.reference
        storageRootRef.child(Constants.IMAGES_FOLDER).child(NodeNames.PHOTO).child(talk.photoName)
            .downloadUrl.addOnSuccessListener { uri ->
                Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(holder.binding.imageViewProfile)
            }
        holder.binding.textViewName.text = talk.userName
        if (talk.unreadCount != "0"){
            holder.binding.relativeLayoutUnread.visibility = View.VISIBLE
            holder.binding.textViewUnreadCount.text = talk.unreadCount
        }else{
            holder.binding.relativeLayoutUnread.visibility = View.GONE
        }
        holder.binding.linearLayoutTalkList.setOnClickListener{
            val intent = Intent(context, TalkActivity::class.java)
            intent.putExtra(Extras.USER_ID, talk.userId)
            intent.putExtra(Extras.USER_NAME, talk.userName)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return allTalk.size
    }
}