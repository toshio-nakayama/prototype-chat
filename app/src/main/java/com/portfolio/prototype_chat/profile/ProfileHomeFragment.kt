package com.portfolio.prototype_chat.profile

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
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
import com.portfolio.prototype_chat.common.Extras
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.FragmentProfileHomeBinding
import com.portfolio.prototype_chat.signup.User

class ProfileHomeFragment : Fragment() {

    private var _binding: FragmentProfileHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentUser: FirebaseUser
    private lateinit var dbRootRef: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var storageRootRef: StorageReference
    private var userEventListener: ValueEventListener? = null

    internal var callback: LogoutDetectionListener? = null

    companion object {
        fun newInstance() = ProfileHomeFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as? LogoutDetectionListener
        callback ?: throw ClassCastException("$context must implement LogoutDetectionListener")
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = Firebase.auth.currentUser!!
        dbRootRef = Firebase.database.reference
        dbRefUser = dbRootRef.child(NodeNames.USERS)
        storageRootRef = Firebase.storage.reference

        setProfile()
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_settings, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                Navigation.findNavController(requireView()).navigate(R.id.editProfileFragment)
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        binding.imageButtonLogout.setOnClickListener { callback?.onLogout() }
        binding.textViewStatusMessage.setOnClickListener { onStatusMessageClick() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userEventListener = null
    }

    private fun setProfile() {
        val userId = currentUser.uid
        userEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.textViewName.text = user?.name
                user?.statusMessage?.let {
                    binding.textViewStatusMessage.text = user.statusMessage
                }
                Glide.with(requireContext())
                    .load(currentUser.photoUrl)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(binding.imageViewProfile)
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
        storageRootRef.child(Constants.IMAGES_FOLDER).child(NodeNames.BACKGROUND_PHOTO_URI_PATH)
            .child(photoName).downloadUrl.addOnSuccessListener { uri ->
                Glide.with(requireContext())
                    .load(uri)
                    .placeholder(R.drawable.default_background)
                    .error(R.drawable.default_background)
                    .into(binding.imageViewBackgroundPhoto)
            }
    }

    private fun onStatusMessageClick() {
        if (binding.textViewStatusMessage.text.isNotEmpty()) {
            val dialogFragment = DisplayMessageFragment()
            val args = Bundle()
            val message = binding.textViewStatusMessage.text.toString()
            args.putString(Extras.MESSAGE, message)
            dialogFragment.arguments = args
            dialogFragment.show(parentFragmentManager, DisplayMessageFragment.DIALOG_TAG)
        }
    }

    interface LogoutDetectionListener {
        fun onLogout()
    }

}