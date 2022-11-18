package com.portfolio.prototype_chat.home_ui.talk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.common.Constants
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.FragmentTalkBinding

class TalkFragment : Fragment() {

    private var _binding: FragmentTalkBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TalkRVAdapter
    private lateinit var allTalk: ArrayList<Talk>
    private lateinit var currentUser: FirebaseUser
    private lateinit var dbRootRef: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var dbRefTalk: DatabaseReference
    private lateinit var query: Query


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentUser = Firebase.auth.currentUser!!
        dbRootRef = Firebase.database.reference
        dbRefUser = dbRootRef.child(NodeNames.USERS)
        dbRefTalk = dbRootRef.child(NodeNames.TALK)

        allTalk = arrayListOf()
        adapter = TalkRVAdapter(requireContext(), allTalk)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.recyclerViewTalkList.layoutManager = linearLayoutManager
        binding.recyclerViewTalkList.adapter = adapter
        val userId = currentUser.uid
        query = dbRefTalk.child(userId).orderByChild(NodeNames.TIME_STAMP)
        query.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.key?.run { updateList(snapshot, this) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.key?.run { updateList(snapshot, this) }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun updateList(snapshot: DataSnapshot, userId: String) {
        dbRefUser.child(userId).addListenerForSingleValueEvent(object : ValueEventListener{
            val unreadCount = snapshot.child(NodeNames.UNREAD_COUNT).value?.toString()
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child(NodeNames.NAME).value?.toString()?:""
                val photoName = userId + Constants.EXT_JPG
                val talk = Talk(userId = userId, userName = userName, photoName = photoName)
                allTalk.add(talk)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}