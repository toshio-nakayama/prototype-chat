package com.portfolio.prototype_chat.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.FragmentProfileHomeBinding
import com.portfolio.prototype_chat.models.db.User
import com.portfolio.prototype_chat.utils.Constants
import com.portfolio.prototype_chat.utils.NodeNames
import com.portfolio.prototype_chat.utils.glideSupport
import com.portfolio.prototype_chat.viewmodels.ProfileHomeViewModel

class ProfileHomeFragment : Fragment() {

    private var _binding: FragmentProfileHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var storageRootRef: StorageReference
    private var callback: LogoutDetectionListener? = null
    private val viewModel: ProfileHomeViewModel by viewModels()

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
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storageRootRef = Firebase.storage.reference
        viewModel.userLiveData.observe(viewLifecycleOwner) { setProfile(it) }
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
        binding.imagebuttonLogout.setOnClickListener { callback?.onLogout() }
        binding.textStatusmessage.setOnClickListener { onStatusMessageClick() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setProfile(user: User) {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            binding.textName.text = user.name
            binding.textStatusmessage.text = user.statusMessage
            glideSupport(requireContext(),
                it.photoUrl, R.drawable.default_profile, binding.circularimageProfile)
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


    private fun onStatusMessageClick() {
        if (binding.textStatusmessage.text.isNotEmpty()) {
            val message = binding.textStatusmessage.text.toString()
            val dialogFragment = MessageDisplayFragment.newInstance(message)
            dialogFragment.show(parentFragmentManager, MessageDisplayFragment.DIALOG_TAG)
        }
    }

    interface LogoutDetectionListener {
        fun onLogout()
    }

}