package com.portfolio.prototype_chat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.portfolio.prototype_chat.databinding.FragmentFriendsBinding
import com.portfolio.prototype_chat.models.db.Friend
import com.portfolio.prototype_chat.utils.notifyObserver
import com.portfolio.prototype_chat.viewmodels.FriendsViewModel
import com.portfolio.prototype_chat.views.adapters.FriendsAdapter

class FriendsFragment : Fragment() {
    
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendsViewModel by viewModels()
    
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.shimmerLayout.startShimmer()
        viewModel.friendListLiveData.observe(viewLifecycleOwner) {
            initRecyclerView(it)
        }
        viewModel.friendLiveData.observe(viewLifecycleOwner) {
            viewModel.friendListLiveData.value?.add(it)
            viewModel.friendListLiveData.notifyObserver()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun initRecyclerView(list: List<Friend>) {
        binding.recyclerFriends.layoutManager = LinearLayoutManager(context).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        val friendsAdapter = FriendsAdapter(requireContext())
        friendsAdapter.submitList(list)
        binding.recyclerFriends.adapter = friendsAdapter
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.isVisible = false
    }
}