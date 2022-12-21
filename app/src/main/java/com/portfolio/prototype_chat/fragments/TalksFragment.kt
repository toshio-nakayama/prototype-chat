package com.portfolio.prototype_chat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.portfolio.prototype_chat.databinding.FragmentTalksBinding
import com.portfolio.prototype_chat.utils.notifyObserver
import com.portfolio.prototype_chat.viewmodels.TalksViewModel
import com.portfolio.prototype_chat.views.adapters.TalksListAdapter

class TalksFragment : Fragment() {

    private var _binding: FragmentTalksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TalksViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTalksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerTalks.layoutManager = LinearLayoutManager(context).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        val talksListAdapter = TalksListAdapter(requireContext())
        binding.recyclerTalks.adapter = talksListAdapter
        viewModel.talkListLiveData.observe(viewLifecycleOwner) {
            talksListAdapter.submitList(it)
        }
        viewModel.talkLiveData.observe(viewLifecycleOwner) {
            viewModel.talkListLiveData.value?.add(it)
            viewModel.talkListLiveData.notifyObserver()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

