package com.portfolio.prototype_chat.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.FragmentMessageEditBinding
import com.portfolio.prototype_chat.utils.Constants
import com.portfolio.prototype_chat.utils.NodeNames
import kotlinx.android.synthetic.main.fragment_message_edit.*

class MessageEditFragment : DialogFragment() {
    
    interface NoticeDialogListener {
        fun onSaveClick(key: String, text: String)
    }
    
    private lateinit var listener: NoticeDialogListener
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var rootRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var binding: FragmentMessageEditBinding
    
    companion object {
        const val NO_USE = -1
        const val TAG_DIALOG = "editMessageFragment"
        const val PROPERTY_NAME = "propertyName"
        const val CONTENTS = "contents"
        const val MAX_LENGTH = "maxLength"
        const val MIN_LENGTH = "minLength"
        const val INPUT_TYPE = "inputType"
        
        fun newInstance(
            key: String,
            contents: String,
            maxLength: Int,
            minLength: Int,
            inputType: Int,
        ) =
            MessageEditFragment().apply {
                arguments = Bundle().apply {
                    putString(PROPERTY_NAME, key)
                    putString(CONTENTS, contents)
                    putInt(MAX_LENGTH, maxLength)
                    putInt(MIN_LENGTH, minLength)
                    putInt(INPUT_TYPE, inputType)
                }
            }
    }
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as NoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$parentFragment must implement NoticeDialogListener")
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentMessageEditBinding.inflate(layoutInflater)
        val dialog =
            activity?.let { Dialog(it) } ?: throw IllegalStateException("Activity cannot be null")
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return dialog
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        auth = Firebase.auth
        currentUser = auth.currentUser!!
        rootRef = Firebase.database.reference
        userRef = rootRef.child(NodeNames.USERS)
        
        val key = arguments?.getString(PROPERTY_NAME)!!
        val contents = arguments?.getString(CONTENTS)!!
        val maxLength = arguments?.getInt(MAX_LENGTH)!!
        val minLength = arguments?.getInt(MIN_LENGTH)!!
        val inputType = arguments?.getInt(INPUT_TYPE)!!
        setupEditText(contents, maxLength, minLength, inputType)
        button_save.setOnClickListener {
            val text = edit_input.text.toString().trim()
            listener.onSaveClick(key, text)
            dismiss()
        }
        imagebutton_close.setOnClickListener { dismiss() }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    
    private fun setupEditText(contents: String, maxLength: Int, minLength: Int, inputType: Int) {
        edit_input.setText(contents)
        val wordCount = contents.length
        text_count.text = getString(R.string.word_count, wordCount, maxLength)
        edit_input.inputType = inputType
        edit_input.filters = arrayOf(InputFilter.LengthFilter(maxLength))
        edit_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val text = p0.toString()
                text_count.text = getString(R.string.word_count, text.length, maxLength)
                if (minLength == NO_USE) {
                    return
                }
                if (text.length < minLength) {
                    textinput_message.error = getString(
                        R.string.less_characters_warning,
                        Constants.MIN_LENGTH_NAME.toString()
                    )
                    button_save.isEnabled = false
                } else {
                    textinput_message.error = null
                    button_save.isEnabled = true
                }
            }
        })
    }
    
}