package com.portfolio.prototype_chat.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.MessageLayoutBinding
import com.portfolio.prototype_chat.models.db.Message
import com.portfolio.prototype_chat.models.db.User
import com.portfolio.prototype_chat.utils.Constants
import com.portfolio.prototype_chat.utils.NodeNames
import com.portfolio.prototype_chat.utils.timestampToString
import com.portfolio.prototype_chat.views.util.setImage

class MessagesAdapter(val context: Context, private val messageList: List<Message>) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    val rootRef: DatabaseReference = Firebase.database.reference
    
    inner class ViewHolder(val binding: MessageLayoutBinding) : RecyclerView.ViewHolder(binding
        .root) {
        
        fun bind(item: Message) {
            val currentUserId = Firebase.auth.currentUser!!.uid
            val fromUserId = item.messageFrom
            val messageTime = timestampToString(item.messageTime, Constants.DATE_TIME_PATTERN)
            if (fromUserId == currentUserId) {
                binding.linearSent.visibility = View.VISIBLE
                binding.linearReceived.visibility = View.GONE
                binding.textSentmessage.text = item.message
                binding.textSenttime.text = messageTime
            } else {
                binding.linearReceived.visibility = View.VISIBLE
                binding.linearSent.visibility = View.GONE
                binding.textReceivedmessage.text = item.message
                binding.textReceivedtime.text = messageTime
                rootRef.child(NodeNames.USERS).child(fromUserId!!).get().addOnSuccessListener {
                    val photo = it.getValue(User::class.java)?.photo
                    photo?.let { uri ->
                        setImage(context, uri, R.drawable.default_profile,
                            binding.circularimageProfile)
                    }
                }
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MessageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messageList[position])
    }
    
    override fun getItemCount(): Int {
        return messageList.size
    }
    
}
