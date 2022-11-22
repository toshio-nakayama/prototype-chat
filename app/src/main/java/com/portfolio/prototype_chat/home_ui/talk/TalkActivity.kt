package com.portfolio.prototype_chat.home_ui.talk

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
import com.portfolio.prototype_chat.common.Extras
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.ActivityTalkBinding
import com.portfolio.prototype_chat.util.updateTalkDetails

class TalkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTalkBinding
    private lateinit var currentUser: FirebaseUser
    private lateinit var dbRootRef: DatabaseReference
    private lateinit var adapter: MessageRVAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var talkUserId: String
    private lateinit var currentUserId: String
    private var currentPage = 1

    companion object {
        const val RECORD_PER_PAGE = 30
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTalkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRootRef = Firebase.database.reference
        currentUser = Firebase.auth.currentUser!!
        talkUserId = intent.getStringExtra(Extras.USER_ID)!!
        currentUserId = currentUser.uid
        binding.imageViewSend.setOnClickListener {
            val messagePushRef =
                dbRootRef.child(NodeNames.MESSAGE).child(currentUserId).child(talkUserId).push()
            val pushId = messagePushRef.key.toString()
            sendMessage(binding.editTextMessage.text.toString(), pushId)
        }
        messageList = arrayListOf()
        adapter = MessageRVAdapter(messageList)
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.adapter = adapter
        loadMessage()
        binding.recyclerViewMessages.scrollToPosition(messageList.size - 1)
        binding.swipeRefreshLayoutMessages.setOnRefreshListener {
            currentPage++
            loadMessage()
        }
    }

    override fun onBackPressed() {
        dbRootRef.child(NodeNames.TALK).child(currentUserId).child(talkUserId)
            .child(NodeNames.UNREAD_COUNT).setValue(0)
        super.onBackPressed()
    }

    private fun sendMessage(message: String, pushId: String) {
        val currentUserId = currentUser.uid
        val messageModel = Message(pushId, message, currentUserId)
        val postValues = messageModel.toMap()
        val currentUserRoot = "${NodeNames.MESSAGES}/$currentUserId/$talkUserId/"
        val talkUserRoot = "${NodeNames.MESSAGES}/$talkUserId/$currentUserId/"
        val childUpdates = hashMapOf<String, Any>(
            "$currentUserRoot$pushId" to postValues,
            "$talkUserRoot$pushId" to postValues
        )
        dbRootRef.updateChildren(
            childUpdates
        ) { error, _ ->
            error ?: run { updateTalkDetails(this@TalkActivity, currentUserId, talkUserId) }
        }
    }

    private fun loadMessage() {
        val currentUserId = currentUser.uid
        messageList.clear()
        val messageRef = dbRootRef.child(NodeNames.MESSAGES).child(currentUserId).child(talkUserId)
        currentPage = RECORD_PER_PAGE
        val query = messageRef.limitToLast(currentPage)
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let { messageList.add(message) }
                adapter.notifyDataSetChanged()
                binding.recyclerViewMessages.scrollToPosition(messageList.size)
                binding.swipeRefreshLayoutMessages.isRefreshing = false
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                loadMessage()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
                binding.swipeRefreshLayoutMessages.isRefreshing = false
            }

        })
    }
}