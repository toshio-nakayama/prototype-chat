package com.portfolio.prototype_chat.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.NodeNames
import com.portfolio.prototype_chat.databinding.ActivityMessagesBinding
import com.portfolio.prototype_chat.views.adapters.MessagesAdapter
import com.portfolio.prototype_chat.models.db.Message
import com.portfolio.prototype_chat.utils.updateTalkDetails

class MessagesActivity : AppCompatActivity() {

    private lateinit var currentUser: FirebaseUser
    private lateinit var rootRef: DatabaseReference
    private lateinit var adapter: MessagesAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var friendUserId: String
    private lateinit var currentUserId: String
    private var currentPage = 1
    private lateinit var binding: ActivityMessagesBinding

    companion object {
        const val RECORD_PER_PAGE = 30
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rootRef = Firebase.database.reference
        currentUser = Firebase.auth.currentUser!!
        friendUserId = intent.getStringExtra(Extras.USER_ID)!!
        currentUserId = currentUser.uid
        binding.imageSend.setOnClickListener {
            val messagePushRef =
                rootRef.child(NodeNames.MESSAGE).child(currentUserId).child(friendUserId).push()
            val pushId = messagePushRef.key.toString()
            sendMessage(binding.editMessage.text.toString(), pushId)
        }
        messageList = arrayListOf()
        adapter = MessagesAdapter(messageList)
        binding.recyclerMessages.layoutManager = LinearLayoutManager(this)
        binding.recyclerMessages.adapter = adapter
        loadMessage()
        binding.recyclerMessages.scrollToPosition(messageList.size - 1)
        binding.swiperefreshMessages.setOnRefreshListener {
            currentPage++
            loadMessage()
        }
    }

    override fun onBackPressed() {
        rootRef.child(NodeNames.TALK).child(currentUserId).child(friendUserId)
            .child(NodeNames.UNREAD_COUNT).setValue(0)
        super.onBackPressed()
    }

    private fun sendMessage(message: String, pushId: String) {
        val currentUserId = currentUser.uid
        val messageModel = Message(pushId, message, currentUserId)
        val postValues = messageModel.toMap()
        val currentUserRoot = "${NodeNames.MESSAGES}/$currentUserId/$friendUserId/"
        val talkUserRoot = "${NodeNames.MESSAGES}/$friendUserId/$currentUserId/"
        val childUpdates = hashMapOf<String, Any>(
            "$currentUserRoot$pushId" to postValues,
            "$talkUserRoot$pushId" to postValues
        )
        rootRef.updateChildren(
            childUpdates
        ) { error, _ ->
            error ?: run { updateTalkDetails(currentUserId, friendUserId) }
        }
    }

    private fun loadMessage() {
        val currentUserId = currentUser.uid
        messageList.clear()
        val messageRef = rootRef.child(NodeNames.MESSAGES).child(currentUserId).child(friendUserId)
        currentPage = RECORD_PER_PAGE
        val query = messageRef.limitToLast(currentPage)
        query.addChildEventListener(object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let { messageList.add(message) }
                adapter.notifyDataSetChanged()
                binding.recyclerMessages.scrollToPosition(messageList.size)
                binding.swiperefreshMessages.isRefreshing = false
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                loadMessage()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
                binding.swiperefreshMessages.isRefreshing = false
            }

        })
    }
}