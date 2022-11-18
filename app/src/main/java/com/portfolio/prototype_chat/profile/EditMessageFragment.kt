package com.portfolio.prototype_chat.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.portfolio.prototype_chat.R

class EditMessageFragment : Fragment() {

    companion object {
        fun newInstance() = EditMessageFragment()
    }

    private lateinit var viewModel: EditMessageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_message, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditMessageViewModel::class.java)
        // TODO: Use the ViewModel
    }

}