package com.portfolio.prototype_chat.home_ui.talk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.portfolio.prototype_chat.databinding.FragmentTalksBinding
import com.portfolio.prototype_chat.util.notifyObserver

class TalksFragment : Fragment() {

    private var _binding: FragmentTalksBinding? = null
    private val binding get() = _binding!!
    private val talkListAdapter: TalksListAdapter by lazy { TalksListAdapter(requireContext()) }
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
        binding.apply {
            recyclerTalks.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    reverseLayout = true
                    stackFromEnd = true
                }
                adapter = talkListAdapter
            }
        }
        viewModel.talkList.observe(this.viewLifecycleOwner) {
            talkListAdapter.submitList(it)
        }
        viewModel.talkLiveData.observe(viewLifecycleOwner) {
            viewModel.talkList.value?.add(it)
            viewModel.talkList.notifyObserver()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

