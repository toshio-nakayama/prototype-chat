package com.portfolio.prototype_chat.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.utils.Constants
import com.portfolio.prototype_chat.databinding.MessageLayoutBinding
import com.portfolio.prototype_chat.models.db.Message
import com.portfolio.prototype_chat.utils.timestampToString

class MessagesAdapter(private val messageList: List<Message>) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    class ViewHolder(item: MessageLayoutBinding) : RecyclerView.ViewHolder(item.root) {
        val binding = item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MessageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        val currentUserId = Firebase.auth.currentUser!!.uid
        val fromUserId = message.messageFrom
        val messageTime = timestampToString(message.messageTime, Constants.DATE_TIME_PATTERN)
        if (fromUserId == currentUserId) {
            holder.binding.linearSent.visibility = View.VISIBLE
            holder.binding.linearReceived.visibility = View.GONE
            holder.binding.textSentmessage.text = message.message
            holder.binding.textSenttime.text = messageTime
        } else {
            holder.binding.linearReceived.visibility = View.VISIBLE
            holder.binding.linearSent.visibility = View.GONE
            holder.binding.textReceivedmessage.text = message.message
            holder.binding.textReceivedtime.text = messageTime
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

}
