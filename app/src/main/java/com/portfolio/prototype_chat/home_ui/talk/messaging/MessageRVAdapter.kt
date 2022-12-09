package com.portfolio.prototype_chat.home_ui.talk.messaging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.common.Constants
import com.portfolio.prototype_chat.databinding.MessageLayoutBinding
import com.portfolio.prototype_chat.util.timestampToString

class MessageRVAdapter (private val messageList: List<Message>) :
    RecyclerView.Adapter<MessageRVAdapter.ViewHolder>() {

    class ViewHolder(@NonNull item: MessageLayoutBinding) : RecyclerView.ViewHolder(item.root) {
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
            holder.binding.linearLayoutSent.visibility = View.VISIBLE
            holder.binding.linearLayoutReceived.visibility = View.GONE
            holder.binding.textViewSentMessage.text = message.message
            holder.binding.textViewSentMessageTime.text = messageTime
        } else {
            holder.binding.linearLayoutReceived.visibility = View.VISIBLE
            holder.binding.linearLayoutSent.visibility = View.GONE
            holder.binding.textViewReceivedMessage.text = message.message
            holder.binding.textViewReceivedMessageTime.text = messageTime
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

}
