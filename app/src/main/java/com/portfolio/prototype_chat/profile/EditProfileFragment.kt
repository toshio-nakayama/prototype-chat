package com.portfolio.prototype_chat.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
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
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.FragmentEditProfileBinding
import com.portfolio.prototype_chat.signup.User

class EditProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var dbRootRef: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var storageRootRef: StorageReference
    private  var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = EditProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
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
        setBackgroundPhoto()
        binding.textViewName.setOnClickListener {}
        binding.textViewStatusMessage.setOnClickListener {}
        binding.buttonAddPhoto.setOnClickListener {}
        binding.buttonAddBackgroundPhoto.setOnClickListener {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setProfile() {
        val userId = currentUser.uid
        dbRefUser.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textViewName.text = currentUser.displayName
                val user = snapshot.getValue(User::class.java)
                user?.statusMessage?.run {
                    binding.textViewStatusMessage.text = user.statusMessage
                } ?: run { binding.textViewStatusMessage.text = getString(R.string.not_set) }
                Glide.with(requireContext())
                    .load(currentUser.photoUrl)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(binding.imageViewProfile)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setBackgroundPhoto() {
        val userId = currentUser.uid
        val photoName = userId + Constants.EXT_JPG
        storageRootRef.child(Constants.IMAGES_FOLDER).child(NodeNames.BACKGROUND_PHOTO)
            .child(photoName).downloadUrl.addOnSuccessListener { uri ->
                Glide.with(requireContext())
                    .load(uri)
                    .placeholder(R.drawable.default_background)
                    .error(R.drawable.default_background)
                    .into(binding.imageViewBackgroundPhoto)
            }
    }

    fun pickImage() {}

    fun pickBackgroundImage() {}

    val takePhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imageViewProfile.setImageURI(it)
            updatePhoto(it)
        }
    }

    private fun updatePhoto(uri: Uri) {

    }

    val takeBackGroundPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imageViewBackgroundPhoto.setImageURI(it)
            updateBackgroundPhoto(uri)
        }
    }

    private fun updateBackgroundPhoto(uri: Uri) {

    }

}