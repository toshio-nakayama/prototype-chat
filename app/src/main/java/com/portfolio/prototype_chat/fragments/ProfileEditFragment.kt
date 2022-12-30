package com.portfolio.prototype_chat.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.FragmentProfileEditBinding
import com.portfolio.prototype_chat.utils.Constants
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.NodeNames
import com.portfolio.prototype_chat.utils.UpdateUI
import com.portfolio.prototype_chat.viewmodels.ProfileEditViewModel

class ProfileEditFragment : Fragment(), MessageEditFragment.NoticeDialogListener {
    
    private lateinit var rootRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var storageRootRef: StorageReference
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileEditViewModel by viewModels()
    
    companion object {
        const val KEY_NAME = "name"
        const val KEY_STATUS_MESSAGE = "statusMessage"
        const val NAME_MIN_LENGTH = 1
        const val NAME_MAX_LENGTH = 20
        const val STATUS_MESSAGE_MAX_LENGTH = 100
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        
        binding.textName.setOnClickListener {
            val contents = binding.textName.text.toString()
            val dialogFragment = MessageEditFragment.newInstance(KEY_NAME,
                contents,
                NAME_MAX_LENGTH,
                NAME_MIN_LENGTH,
                InputType.TYPE_CLASS_TEXT)
            dialogFragment.show(childFragmentManager, MessageEditFragment.TAG_DIALOG)
        }
        binding.textStatusmessage.setOnClickListener {
            val contents = binding.textStatusmessage.text.toString()
            val dialogFragment = MessageEditFragment.newInstance(KEY_STATUS_MESSAGE,
                contents,
                STATUS_MESSAGE_MAX_LENGTH,
                MessageEditFragment.NO_USE,
                InputType.TYPE_TEXT_FLAG_MULTI_LINE)
            dialogFragment.show(childFragmentManager, MessageEditFragment.TAG_DIALOG)
        }
        
        binding.buttonPick.setOnClickListener { pickPhoto() }
        binding.buttonPickbackground.setOnClickListener { pickBackgroundPhoto() }
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        rootRef = Firebase.database.reference
        userRef = rootRef.child(NodeNames.USERS)
        storageRootRef = Firebase.storage.reference
        viewModel.userLiveData.observe(viewLifecycleOwner) {
            UpdateUI(handler).apply {
                setTextAsync(binding.textName, it.name)
                setTextAsync(binding.textStatusmessage, it.statusMessage)
                setImageAsync(requireContext(),
                    it.photo,
                    R.drawable.default_profile,
                    binding.circularimageProfile)
                setImageAsync(requireContext(), it.backgroundPhoto, R.drawable
                    .default_background, binding.imageBackground)
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onSaveClick(key: String, text: String) {
        when (key) {
            Extras.NAME -> updateName(text)
            Extras.STATUS_MESSAGE -> updateStatusMessage(text)
        }
    }
    
    
    private fun pickPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getPhoto.launch(intent)
    }
    
    private fun pickBackgroundPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getBackgroundPhoto.launch(intent)
    }
    
    private fun updatePhoto(uri: Uri) {
        val currentUser = Firebase.auth.currentUser ?: return
        val hostId = currentUser.uid
        val photo = hostId + Constants.EXT_JPG
        val fgRef = storageRootRef.child(Constants.IMAGES).child(NodeNames.PHOTO).child(photo)
        val uploadTask = fgRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            fgRef.downloadUrl.addOnSuccessListener { uri ->
                val profileUpdates = userProfileChangeRequest { photoUri = uri }
                currentUser.updateProfile(profileUpdates).addOnSuccessListener {
                    userRef.child(hostId).child(NodeNames.PHOTO).setValue(fgRef.toString())
                }
            }
        }
    }
    
    private fun updateBackgroundPhoto(uri: Uri) {
        val currentUser = Firebase.auth.currentUser ?: return
        val hostId = currentUser.uid
        val photo = hostId + Constants.EXT_JPG
        val fileRef = storageRootRef.child(Constants.IMAGES).child(NodeNames.BACKGROUND_PHOTO)
            .child(photo)
        val uploadTask = fileRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener {
                userRef.child(hostId).child(NodeNames.BACKGROUND_PHOTO)
                    .setValue(fileRef.toString())
            }
        }
    }
    
    private fun updateName(name: String) {
        val currentUser = Firebase.auth.currentUser ?: return
        val hostId = currentUser.uid
        val profileUpdates = userProfileChangeRequest { displayName = name }
        currentUser.updateProfile(profileUpdates).addOnSuccessListener {
            userRef.child(hostId).child(NodeNames.NAME).setValue(name)
        }
    }
    
    private fun updateStatusMessage(message: String) {
        val currentUser = Firebase.auth.currentUser ?: return
        val hostId = currentUser.uid
        userRef.child(hostId).child(NodeNames.STATUS_MESSAGE).setValue(message)
    }
    
    private val getPhoto =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    binding.circularimageProfile.setImageURI(it)
                    updatePhoto(it)
                }
            }
        }
    
    private val getBackgroundPhoto =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    binding.imageBackground.setImageURI(it)
                    updateBackgroundPhoto(it)
                }
            }
        }
    
}