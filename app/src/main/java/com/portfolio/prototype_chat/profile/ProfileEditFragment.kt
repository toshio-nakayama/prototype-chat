package com.portfolio.prototype_chat.profile

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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.common.Constants
import com.portfolio.prototype_chat.common.Extras
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.FragmentProfileEditBinding
import com.portfolio.prototype_chat.signup.User

class ProfileEditFragment : Fragment(), MessageEditFragment.NoticeDialogListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var dbRootRef: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var storageRootRef: StorageReference
    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!
    private var userEventListener: ValueEventListener? = null

    companion object {
        fun newInstance() = ProfileEditFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        binding.textName.setOnClickListener {
            val dialogFragment = MessageEditFragment()
            val args = Bundle()
            args.putString(Extras.PROPERTY_NAME, Extras.NAME)
            args.putString(Extras.CONTENTS, binding.textName.text.toString())
            args.putInt(Extras.MAX_LENGTH, 20)
            args.putInt(Extras.MIN_LENGTH, 1)
            args.putInt(Extras.INPUT_TYPE, InputType.TYPE_CLASS_TEXT)
            childFragmentManager.setFragmentResult(Extras.REQUEST_KEY, args)
            dialogFragment.show(childFragmentManager, MessageEditFragment.DIALOG_TAG)
        }
        binding.textStatusmessage.setOnClickListener {
            val dialogFragment = MessageEditFragment()
            val args = Bundle()
            args.putString(Extras.PROPERTY_NAME, Extras.STATUS_MESSAGE)
            args.putString(Extras.CONTENTS, binding.textStatusmessage.text.toString())
            args.putInt(Extras.MAX_LENGTH, 100)
            args.putInt(Extras.MIN_LENGTH, MessageEditFragment.NO_USE)
            args.putInt(Extras.INPUT_TYPE, InputType.TYPE_TEXT_FLAG_MULTI_LINE)
            childFragmentManager.setFragmentResult(Extras.REQUEST_KEY, args)
            dialogFragment.show(childFragmentManager, MessageEditFragment.DIALOG_TAG)
        }
        binding.buttonPick.setOnClickListener { pickImage() }
        binding.buttonPickbackground.setOnClickListener { pickBackgroundImage() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        currentUser = auth.currentUser!!
        dbRootRef = Firebase.database.reference
        dbRefUser = dbRootRef.child(NodeNames.USERS)
        storageRootRef = Firebase.storage.reference
        setProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        userEventListener = null
    }


    private fun setProfile() {
        val userId = currentUser.uid
        userEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.textName.text = user?.name
                user?.statusMessage?.run {
                    binding.textName.text = user.statusMessage
                } ?: run { binding.textStatusmessage.text = getString(R.string.not_set) }
                Glide.with(requireContext())
                    .load(currentUser.photoUrl)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(binding.circularimageProfile)
                setBackgroundPhoto()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        dbRefUser.child(userId).addValueEventListener(userEventListener!!)
    }

    private fun setBackgroundPhoto() {
        val userId = currentUser.uid
        val photoName = userId + Constants.EXT_JPG
        storageRootRef.child(Constants.IMAGES).child(NodeNames.BACKGROUND_PHOTO)
            .child(photoName).downloadUrl.addOnSuccessListener { uri ->
                Glide.with(requireContext())
                    .load(uri)
                    .placeholder(R.drawable.default_background)
                    .error(R.drawable.default_background)
                    .into(binding.imageBackground)
            }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        takePhoto.launch(intent)
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                binding.circularimageProfile.setImageURI(it)
                updatePhoto(it)
            }
        }
    }

    private fun pickBackgroundImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        takeBackGroundPhoto.launch(intent)
    }


    private fun updatePhoto(uri: Uri) {
        val userId = currentUser.uid
        val photoName = userId + Constants.EXT_JPG
        val fileRef =
            storageRootRef.child(Constants.IMAGES).child(NodeNames.PHOTO)
                .child(photoName)
        val uploadTask = fileRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                val profileUpdates = userProfileChangeRequest {
                    photoUri = uri
                }
                currentUser.updateProfile(profileUpdates).addOnSuccessListener {
                    dbRefUser.child(userId).child(NodeNames.PHOTO).setValue(uri.path)
                }
            }
        }
    }

    private val takeBackGroundPhoto = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                binding.imageBackground.setImageURI(it)
                updateBackgroundPhoto(it)
            }
        }
    }

    private fun updateBackgroundPhoto(uri: Uri) {
        val userId = currentUser.uid
        val photoName = userId + Constants.EXT_JPG
        val fileRef =
            storageRootRef.child(Constants.IMAGES).child(NodeNames.BACKGROUND_PHOTO)
                .child(photoName)
        val uploadTask = fileRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                dbRefUser.child(userId).child(NodeNames.BACKGROUND_PHOTO)
                    .setValue(uri.path)
            }
        }
    }

    override fun onSaveClick(key: String, text: String) {
        when (key) {
            Extras.NAME -> updateName(text)
            Extras.STATUS_MESSAGE -> updateStatusMessage(text)
        }
    }

    private fun updateName(name: String) {
        val userId = currentUser.uid
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        currentUser.updateProfile(profileUpdates).addOnSuccessListener {
            dbRefUser.child(userId).child(NodeNames.NAME).setValue(name)
        }
    }

    private fun updateStatusMessage(message: String) {
        val userId = currentUser.uid
        dbRefUser.child(userId).child(NodeNames.STATUS_MESSAGE).setValue(message)
    }


}