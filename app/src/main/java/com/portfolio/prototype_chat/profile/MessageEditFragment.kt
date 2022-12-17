package com.portfolio.prototype_chat.profile

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
import com.portfolio.prototype_chat.common.Constants
import com.portfolio.prototype_chat.common.Extras
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.FragmentMessageEditBinding

class MessageEditFragment : DialogFragment() {

    interface NoticeDialogListener {
        fun onSaveClick(key:String, text: String)
    }

    internal lateinit var listener: NoticeDialogListener

    private lateinit var binding: FragmentMessageEditBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var dbRootRef: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference

    companion object {
        const val NO_USE = -1
        const val DIALOG_TAG = "editMessageFragment"
        fun newInstance() = MessageEditFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as NoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((parentFragment.toString() + "must implement NoticeDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentMessageEditBinding.inflate(layoutInflater)
        val dialog = activity?.let {
            Dialog(it)
        } ?: throw IllegalStateException("Activity cannot be null")
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
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        currentUser = auth.currentUser!!
        dbRootRef = Firebase.database.reference
        dbRefUser = dbRootRef.child(NodeNames.USERS)
        parentFragmentManager.setFragmentResultListener(
            Extras.REQUEST_KEY, this
        ) { _, result ->
            val key = result.getString(Extras.PROPERTY_NAME)!!
            val contents = result.getString(Extras.CONTENTS)!!
            val maxLength = result.getInt(Extras.MAX_LENGTH)
            val minLength = result.getInt(Extras.MIN_LENGTH)
            val inputType = result.getInt(Extras.INPUT_TYPE)
            setupEditText(contents, maxLength, minLength, inputType)
            binding.buttonSave.setOnClickListener {
                val text = binding.editInput.text.toString().trim()
                listener.onSaveClick(key, text)
                dismiss()
            }
            binding.imagebuttonClose.setOnClickListener { dismiss() }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setupEditText(contents: String, maxLength: Int, minLength: Int, inputType: Int) {
        binding.editInput.setText(contents)
        val wordCount = contents.length
        binding.textCount.text = getString(R.string.word_count, wordCount, maxLength)
        binding.editInput.inputType = inputType
        binding.editInput.filters = arrayOf(InputFilter.LengthFilter(maxLength))
        binding.editInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val text = p0.toString()
                binding.textCount.text =
                    getString(R.string.word_count, text.length, maxLength)
                if (minLength == NO_USE) return
                if (text.length < minLength) {
                    if (isAdded) {
                        binding.editInput.error = getString(
                            R.string.less_characters_warning,
                            Constants.MIN_LENGTH_NAME.toString()
                        )
                        binding.buttonSave.isEnabled = false
                    }
                } else {
                    binding.editInput.error = null
                    binding.buttonSave.isEnabled = true
                }
            }
        })
    }

}