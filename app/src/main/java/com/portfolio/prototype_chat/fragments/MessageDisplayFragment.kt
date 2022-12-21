package com.portfolio.prototype_chat.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.portfolio.prototype_chat.databinding.FragmentMessageDisplayBinding


class MessageDisplayFragment : DialogFragment() {

    private lateinit var binding: FragmentMessageDisplayBinding

    companion object {
        const val DIALOG_TAG = "displayMessageFragment"
        private const val PARAM = "param"

        fun newInstance(message: String) =
            MessageDisplayFragment().apply {
                arguments = Bundle().apply {
                    putString(PARAM, message)
                }
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentMessageDisplayBinding.inflate(layoutInflater)
        val dialog = activity?.let {
            Dialog(it)
        } ?: throw IllegalStateException("Activity cannot be null")
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val message = arguments?.getString(PARAM, "")
        binding.textMessage.text = message
        binding.imagebuttonClose.setOnClickListener { dismiss() }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}