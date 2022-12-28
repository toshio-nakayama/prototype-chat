package com.portfolio.prototype_chat.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.activities.ProfileActivity
import com.portfolio.prototype_chat.databinding.FragmentProfileHomeBinding
import com.portfolio.prototype_chat.models.db.User
import com.portfolio.prototype_chat.utils.UpdateUI
import com.portfolio.prototype_chat.utils.glideSupport
import com.portfolio.prototype_chat.viewmodels.ProfileHomeViewModel
import kotlinx.android.synthetic.main.fragment_home.circularimage_profile
import kotlinx.android.synthetic.main.fragment_home.text_name
import kotlinx.android.synthetic.main.fragment_home.text_statusmessage
import kotlinx.android.synthetic.main.fragment_profile_edit.*

class ProfileHomeFragment : Fragment() {
    
    private lateinit var storageRootRef: StorageReference
    private var callback: LogoutDetectionListener? = null
    private val handler = Handler(Looper.getMainLooper())
    private val profileActivity:ProfileActivity? get() = (activity as? ProfileActivity)
    private var _binding: FragmentProfileHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileHomeViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storageRootRef = Firebase.storage.reference
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
        
//        profileActivity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        viewModel.userLiveData.observe(viewLifecycleOwner) {
            UpdateUI(handler).apply {
                setTextAsync(text_name, it.name)
                setTextAsync(text_statusmessage, it.statusMessage)
                setImageAsync(requireContext(),
                    it.photo,
                    R.drawable.default_profile,
                    circularimage_profile)
                setImageAsync(requireContext(), it.backgroundPhoto, R.drawable
                    .default_background, image_background)
            }
            
        }
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
        binding.textStatusmessage.setOnClickListener {
            if (binding.textStatusmessage.text.isNotEmpty()) {
                val message = binding.textStatusmessage.text.toString()
                val dialogFragment = MessageDisplayFragment.newInstance(message)
                dialogFragment.show(parentFragmentManager, MessageDisplayFragment.TAG_DIALOG)
            }
        }
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
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    
    interface LogoutDetectionListener {
        fun onLogout()
    }
    
}