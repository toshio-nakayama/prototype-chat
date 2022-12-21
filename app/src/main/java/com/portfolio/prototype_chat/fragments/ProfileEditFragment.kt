package com.portfolio.prototype_chat.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.FragmentProfileEditBinding
import com.portfolio.prototype_chat.models.db.User
import com.portfolio.prototype_chat.utils.Constants
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.NodeNames
import com.portfolio.prototype_chat.utils.glideSupport
import com.portfolio.prototype_chat.viewmodels.ProfileEditViewModel

class ProfileEditFragment : Fragment(), MessageEditFragment.NoticeDialogListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRootRef: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var storageRootRef: StorageReference
    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!
    private var userEventListener: ValueEventListener? = null
    private val viewModel: ProfileEditViewModel by viewModels()
    
    companion object {
        const val KEY_NAME = "name"
        const val KEY_STATUS_MESSAGE = "statusMessage"
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        binding.textName.setOnClickListener {
            val dialogFragment = MessageEditFragment.newInstance(KEY_NAME,
                binding.textName.text.toString(),
                20,
                1,
                InputType.TYPE_CLASS_TEXT)
            dialogFragment.show(childFragmentManager, MessageEditFragment.DIALOG_TAG)
        }
        binding.textStatusmessage.setOnClickListener {
            val dialogFragment = MessageEditFragment.newInstance(KEY_STATUS_MESSAGE,
                binding.textStatusmessage.text.toString(),
                100,
                MessageEditFragment.NO_USE,
                InputType.TYPE_TEXT_FLAG_MULTI_LINE)
            dialogFragment.show(childFragmentManager, MessageEditFragment.DIALOG_TAG)
        }
        binding.buttonPick.setOnClickListener { pickPhoto() }
        binding.buttonPickbackground.setOnClickListener { pickBackgroundPhoto() }
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        dbRootRef = Firebase.database.reference
        dbRefUser = dbRootRef.child(NodeNames.USERS)
        storageRootRef = Firebase.storage.reference
        viewModel.userLiveData.observe(viewLifecycleOwner) {
            setProfile(it)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        userEventListener = null
    }
    
    override fun onSaveClick(key: String, text: String) {
        when (key) {
            Extras.NAME -> updateName(text)
            Extras.STATUS_MESSAGE -> updateStatusMessage(text)
        }
    }
    
    private fun setProfile(user: User) {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            binding.textName.text = user.name
            binding.textStatusmessage.text = user.statusMessage
            glideSupport(requireContext(),
                it.photoUrl,
                R.drawable.default_profile,
                binding.circularimageProfile)
            val userId = it.uid
            val photoName = userId + Constants.EXT_JPG
            storageRootRef.child(Constants.IMAGES).child(NodeNames.BACKGROUND_PHOTO)
                .child(photoName).downloadUrl.addOnSuccessListener { uri ->
                    glideSupport(requireContext(),
                        uri,
                        R.drawable.default_background,
                        binding.imageBackground)
                }
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
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            val userId = currentUser.uid
            val photoName = userId + Constants.EXT_JPG
            val fileRef =
                storageRootRef.child(Constants.IMAGES).child(NodeNames.PHOTO).child(photoName)
            val uploadTask = fileRef.putFile(uri)
            uploadTask.addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileUpdates = userProfileChangeRequest { photoUri = uri }
                    currentUser.updateProfile(profileUpdates).addOnSuccessListener {
                        dbRefUser.child(userId).child(NodeNames.PHOTO).setValue(uri.path)
                            .addOnSuccessListener { }
                    }
                }
            }
        }
    }
    
    private fun updateBackgroundPhoto(uri: Uri) {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            val userId = currentUser.uid
            val photoName = userId + Constants.EXT_JPG
            val fileRef = storageRootRef.child(Constants.IMAGES).child(NodeNames.BACKGROUND_PHOTO)
                .child(photoName)
            val uploadTask = fileRef.putFile(uri)
            uploadTask.addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener {
                    dbRefUser.child(userId).child(NodeNames.BACKGROUND_PHOTO).setValue(it.path)
                        .addOnSuccessListener { }
                }
            }
        }
    }
    
    private fun updateName(name: String) {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            val userId = currentUser.uid
            val profileUpdates = userProfileChangeRequest { displayName = name }
            currentUser.updateProfile(profileUpdates).addOnSuccessListener {
                dbRefUser.child(userId).child(NodeNames.NAME).setValue(name).addOnSuccessListener {}
            }
        }
    }
    
    private fun updateStatusMessage(message: String) {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            val userId = currentUser.uid
            dbRefUser.child(userId).child(NodeNames.STATUS_MESSAGE).setValue(message)
                .addOnSuccessListener {}
        }
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