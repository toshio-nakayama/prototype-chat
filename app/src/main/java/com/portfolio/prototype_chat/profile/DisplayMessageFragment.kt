package com.portfolio.prototype_chat.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.common.Extras
import com.portfolio.prototype_chat.databinding.FragmentDisplayMessageBinding


class DisplayMessageFragment : DialogFragment() {

    private lateinit var binding: FragmentDisplayMessageBinding

    companion object {
        const val DIALOG_TAG = "displayMessageFragment"
        fun newInstance() = DisplayMessageFragment()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentDisplayMessageBinding.inflate(layoutInflater)
        val dialog = activity?.let {
            Dialog(it)
        }?:throw IllegalStateException("Activity cannot be null")
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val message = arguments?.getString(Extras.MESSAGE, "")
        binding.textViewMessage.text = message
        binding.imageButtonClose.setOnClickListener{dismiss()}
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}