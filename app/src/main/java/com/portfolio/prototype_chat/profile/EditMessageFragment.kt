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
import com.portfolio.prototype_chat.databinding.FragmentEditMessageBinding

class EditMessageFragment : DialogFragment() {

    interface NoticeDialogListener {
        fun onSaveClick(key:String, text: String)
    }

    internal lateinit var listener: NoticeDialogListener

    private lateinit var binding: FragmentEditMessageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var dbRootRef: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference

    companion object {
        const val NO_USE = -1
        const val DIALOG_TAG = "editMessageFragment"
        fun newInstance() = EditMessageFragment()
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
        binding = FragmentEditMessageBinding.inflate(layoutInflater)
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
                val text = binding.editTextInput.text.toString().trim()
                listener.onSaveClick(key, text)
                dismiss()
            }
            binding.imageButtonClose.setOnClickListener { dismiss() }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setupEditText(contents: String, maxLength: Int, minLength: Int, inputType: Int) {
        binding.editTextInput.setText(contents)
        val wordCount = contents.length
        binding.textViewWordCount.text = getString(R.string.word_count, wordCount, maxLength)
        binding.editTextInput.inputType = inputType
        binding.editTextInput.filters = arrayOf(InputFilter.LengthFilter(maxLength))
        binding.editTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val text = p0.toString()
                binding.textViewWordCount.text =
                    getString(R.string.word_count, text.length, maxLength)
                if (minLength == NO_USE) return
                if (text.length < minLength) {
                    if (isAdded) {
                        binding.editTextInput.error = getString(
                            R.string.less_characters_warning,
                            Constants.MIN_LENGTH_NAME.toString()
                        )
                        binding.buttonSave.isEnabled = false
                    }
                } else {
                    binding.editTextInput.error = null
                    binding.buttonSave.isEnabled = true
                }
            }
        })
    }

}