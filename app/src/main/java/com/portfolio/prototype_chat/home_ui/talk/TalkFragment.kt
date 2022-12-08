package com.portfolio.prototype_chat.home_ui.talk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
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
    private lateinit var adapter: TalkListAdapter
    private lateinit var allTalk: ArrayList<Talk>
    private lateinit var currentUser: FirebaseUser
    private lateinit var rootRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var talkRef: DatabaseReference
    private lateinit var query: Query
    private val talkListAdapter: TalkListAdapter by lazy { TalkListAdapter(requireContext()) }
    private val viewModel: TalkViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentUser = Firebase.auth.currentUser!!
        rootRef = Firebase.database.reference
        userRef = rootRef.child(NodeNames.USERS)
        talkRef = rootRef.child(NodeNames.TALK)

        binding.apply {
            recyclerViewTalkList.apply {
                layoutManager = LinearLayoutManager(context)
                    .apply {
                        reverseLayout = true
                        stackFromEnd = true
                    }
                adapter = talkListAdapter
            }
        }

        viewModel.talkLists.observe(this.viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    talkListAdapter.submitList(it)
                }
            }
        }

        viewModel.talkQueryLiveData.observe(viewLifecycleOwner) { snapshot ->
            snapshot.key?.run {
                updateList(snapshot, this)
            }
        }
    }

    private fun updateList(snapshot: DataSnapshot, userId: String) {
        val unreadCount = snapshot.child(NodeNames.UNREAD_COUNT).value.toString()
        val time = snapshot.child(NodeNames.TIME_STAMP).value.toString()
        userRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child(NodeNames.NAME).value.toString()
                val photoName = userId + Constants.EXT_JPG
                val talk = Talk(userId = userId,
                    userName = userName,
                    photoName = photoName,
                    unreadCount = unreadCount,
                    time = time)
                viewModel.talkLists.value?.add(talk)
                viewModel.talkLists.value = viewModel.talkLists.value

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}

