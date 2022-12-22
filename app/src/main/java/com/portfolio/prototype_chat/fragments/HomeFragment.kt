package com.portfolio.prototype_chat.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.activities.AddFriendActivity
import com.portfolio.prototype_chat.activities.ProfileActivity
import com.portfolio.prototype_chat.activities.QRCodeScannerActivity
import com.portfolio.prototype_chat.databinding.FragmentHomeBinding
import com.portfolio.prototype_chat.models.db.User
import com.portfolio.prototype_chat.utils.Extras
import com.portfolio.prototype_chat.utils.NodeNames
import com.portfolio.prototype_chat.utils.ToastGenerator
import com.portfolio.prototype_chat.utils.glideSupport
import com.portfolio.prototype_chat.viewmodels.HomeViewModel

class HomeFragment : Fragment() {
    
    private lateinit var rootRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var talkRef: DatabaseReference
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        rootRef = Firebase.database.reference
        userRef = rootRef.child(NodeNames.USERS)
        talkRef = rootRef.child(NodeNames.TALK)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.userLiveData.observe(viewLifecycleOwner) { updateUI(it) }
        viewModel.talkLiveData.observe(viewLifecycleOwner) {
            val count = it.childrenCount
            binding.textFriendscount.text = count.toString()
        }
        initView()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun initView() {
        binding.textTooltip.startAnimation(AnimationUtils.loadAnimation(context,
            R.anim.updown_animation))
        binding.linearFriendslink.setOnClickListener {
            it.findNavController().navigate(R.id.action_navigation_home_to_navigation_friends)
        }
        
        binding.linearProfile.setOnClickListener {
            startActivity(Intent(activity, ProfileActivity::class.java))
        }
        
        binding.floatingActionButtonAddFriend.setOnClickListener {
            barcodeLauncher.launch(ScanOptions().apply {
                setOrientationLocked(false)
                setBeepEnabled(false)
                captureActivity = QRCodeScannerActivity::class.java
            })
        }
    }
    
    private fun updateUI(user: User) {
        binding.textName.text = user.name
        binding.textStatusmessage.text = user.statusMessage
        Firebase.storage.getReferenceFromUrl(user.photo)
            .downloadUrl.addOnSuccessListener {
                glideSupport(requireContext(),
                    it,
                    R.drawable.default_profile,
                    binding.circularimageProfile)
            }
    }
    
    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> =
        registerForActivityResult(ScanContract()) { result ->
            result.contents?.let {
                mightAddFriend(it)
            }
            
        }
    
    private fun mightAddFriend(id: String) {
        val hostId = Firebase.auth.currentUser?.uid ?: return
        if (id == hostId) {
            ToastGenerator.Builder(requireContext()).resId(R.string.invalid_qr_code).build()
            return
        }
        talkRef.child(hostId).child(id).get().addOnSuccessListener { snapshotTalk ->
            if (snapshotTalk.exists()) {
                ToastGenerator.Builder(requireContext()).resId(R.string.allready_registered).build()
            } else {
                userRef.child(id).get().addOnSuccessListener { snapshotUser ->
                    if (snapshotUser.exists()) {
                        val user = snapshotUser.getValue(User::class.java)
                        user?.let {
                            val intent = Intent(activity, AddFriendActivity::class.java).apply {
                                putExtra(Extras.USER_ID, id)
                            }
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}
