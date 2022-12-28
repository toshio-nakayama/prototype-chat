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
import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : AppCompatActivity() {
    
    private lateinit var currentUser: FirebaseUser
    private lateinit var rootRef: DatabaseReference
    private lateinit var adapter: MessagesAdapter
    private val messageList: ArrayList<Message> = arrayListOf()
    private lateinit var guestId: String
    private lateinit var hostId: String
    private var currentPage = 1
    private val recordPerPage = 30
    private lateinit var binding: ActivityMessagesBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        rootRef = Firebase.database.reference
        currentUser = Firebase.auth.currentUser!!
        hostId = Firebase.auth.currentUser?.uid ?: return
        guestId = intent.getStringExtra(Extras.USER_ID)!!
        adapter = MessagesAdapter(this, messageList)
        recycler_messages.layoutManager = LinearLayoutManager(this)
        recycler_messages.adapter = adapter
        loadMessage()
        rootRef.child(NodeNames.TALK).child(hostId).child(guestId)
            .child(NodeNames.UNREAD_COUNT).setValue(0)
        recycler_messages.scrollToPosition(messageList.size - 1)
        swiperefresh_messages.setOnRefreshListener {
            currentPage++
            loadMessage()
        }
        image_send.setOnClickListener {
            val pushRef =
                rootRef.child(NodeNames.MESSAGE).child(hostId).child(guestId).push()
            val pushId = pushRef.key.toString()
            sendMessage(binding.editMessage.text.toString(), pushId)
        }
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        rootRef.child(NodeNames.TALK).child(hostId).child(guestId)
            .child(NodeNames.UNREAD_COUNT).setValue(0)
    }
    
    private fun sendMessage(message: String, pushId: String) {
        if(message.isBlank()){return}
        val messageModel = Message(pushId, message, hostId)
        val postValues = messageModel.toMap()
        val currentUserRoot = "${NodeNames.MESSAGES}/$hostId/$guestId/"
        val talkUserRoot = "${NodeNames.MESSAGES}/$guestId/$hostId/"
        val childUpdates = hashMapOf<String, Any>(
            "$currentUserRoot$pushId" to postValues,
            "$talkUserRoot$pushId" to postValues
        )
        rootRef.updateChildren(childUpdates) { error, _ ->
            error ?: run { updateTalkDetails(hostId, guestId, message) }
        }
    }
    
    private fun loadMessage() {
        messageList.clear()
        val messageRef = rootRef.child(NodeNames.MESSAGES).child(hostId).child(guestId)
        val query = messageRef.limitToLast(currentPage * recordPerPage)
        query.addChildEventListener(object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let { messageList.add(message) }
                adapter.notifyDataSetChanged()
                binding.recyclerMessages.scrollToPosition(messageList.size -1)
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