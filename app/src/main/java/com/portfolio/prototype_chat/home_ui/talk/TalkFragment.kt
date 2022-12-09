package com.portfolio.prototype_chat.home_ui.talk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.portfolio.prototype_chat.databinding.FragmentTalkBinding
import com.portfolio.prototype_chat.util.notifyObserver

class TalkFragment : Fragment() {

    private var _binding: FragmentTalkBinding? = null
    private val binding get() = _binding!!
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
        binding.apply {
            recyclerViewTalkList.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    reverseLayout = true
                    stackFromEnd = true
                }
                adapter = talkListAdapter
            }
        }
        viewModel.talkList.observe(this.viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    talkListAdapter.submitList(it)
                }
            }
        }
        viewModel.talkLiveData.observe(viewLifecycleOwner) { talk ->
            viewModel.talkList.value?.add(talk)
            viewModel.talkList.notifyObserver()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

