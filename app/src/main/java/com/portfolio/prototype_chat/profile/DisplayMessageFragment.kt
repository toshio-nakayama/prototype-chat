package com.portfolio.prototype_chat.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.portfolio.prototype_chat.R

class DisplayMessageFragment : DialogFragment() {

    companion object {
        const val DIALOG_TAG = "messageDisplayFragment"
        fun newInstance() = DisplayMessageFragment()
    }

    private lateinit var viewModel: DisplayMessageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_display_message, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DisplayMessageViewModel::class.java)
        // TODO: Use the ViewModel
    }

}