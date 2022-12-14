package com.portfolio.prototype_chat.views.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.activities.MessagesActivity
import com.portfolio.prototype_chat.databinding.TalkListLayoutBinding
import com.portfolio.prototype_chat.models.db.Talk
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.formatMessageTime
import com.portfolio.prototype_chat.views.util.setImage

class TalksListAdapter(val context: Context) :
    ListAdapter<Talk, TalksListAdapter.ViewHolder>(DIFF_CALLBACK) {
    
    inner class ViewHolder(val binding: TalkListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: Talk) {
            binding.textName.text = item.userName
            item.photo?.let {
                setImage(context, it, R.drawable.default_profile,
                    binding.circularimageProfile)
            }
            binding.textMessage.text = item.lastMessage
            binding.textTime.text = item.time?.let { formatMessageTime(context, it.toLong()) } ?: ""
            if (item.unreadCount == 0) {
                binding.relativeUnread.visibility = View.GONE
            } else {
                binding.relativeUnread.visibility = View.VISIBLE
                binding.textUnreadcount.text = item.unreadCount.toString()
            }
            binding.linearRow.setOnClickListener {
                val intent = Intent(context, MessagesActivity::class.java).apply {
                    putExtra(Extras.USER_ID, item.userId)
                    putExtra(Extras.USER_NAME, item.userName)
                }
                context.startActivity(intent)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TalkListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    override fun submitList(list: List<Talk>?) {
        super.submitList(list?.let { ArrayList(it) })
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