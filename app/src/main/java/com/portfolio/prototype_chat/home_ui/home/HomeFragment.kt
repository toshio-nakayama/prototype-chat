package com.portfolio.prototype_chat.home_ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.add_friend.AddFriendActivity
import com.portfolio.prototype_chat.common.Constants
import com.portfolio.prototype_chat.common.Extras
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.FragmentHomeBinding
import com.portfolio.prototype_chat.profile.ProfileActivity
import com.portfolio.prototype_chat.qrcode.QRCodeScannerActivity
import com.portfolio.prototype_chat.signup.User
import com.portfolio.prototype_chat.util.ToastGenerator

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var rootRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var talkRef: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rootRef = Firebase.database.reference
        userRef = rootRef.child(NodeNames.USERS)
        talkRef = rootRef.child(NodeNames.TALK)
        currentUser = Firebase.auth.currentUser!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.textViewName.text = it.name
                binding.textViewStatusMessage.text = it.statusMessage
                Glide.with(requireContext())
                    .load(currentUser.photoUrl)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(binding.imageViewProfile)
            }
        }

        viewModel.talkLiveData.observe(viewLifecycleOwner) { snapshot ->
            snapshot?.let {
                val count = it.childrenCount
                binding.textViewFriendsCount.text = count.toString()
            }
        }
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        binding.linearLayoutFriendsList.setOnClickListener {
            it.findNavController().navigate(R.id.action_navigation_home_to_navigation_friends)
        }

        binding.linearLayoutProfile.setOnClickListener {
            startActivity(Intent(activity, ProfileActivity::class.java))
        }

        binding.floatingActionButtonQrScan.setOnClickListener {
            barcodeLauncher.launch(ScanOptions().apply {
                setOrientationLocked(false)
                setBeepEnabled(false)
                captureActivity = QRCodeScannerActivity::class.java
            })
        }
    }

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> =
        registerForActivityResult(ScanContract()) { result ->
            result.contents?.run {
                val userId: String = this
                mightAddFriend(userId)
            }

        }

    private fun mightAddFriend(id: String) {
        val userId = currentUser.uid
        if (id == userId) {
            ToastGenerator.Builder(requireContext()).resId(R.string.invalid_qr_code).build()
            return
        }
        talkRef.child(userId).child(id).get().addOnSuccessListener { snapshotTalk ->
            if (snapshotTalk.exists()) {
                ToastGenerator.Builder(requireContext()).resId(R.string.allready_registered).build()
            } else {
                userRef.child(id).get().addOnSuccessListener { snapshotUser ->
                    if (snapshotUser.exists()) {
                        val user = snapshotUser.getValue(User::class.java)
                        user?.let {
                            val intent = Intent(activity, AddFriendActivity::class.java)
                            intent.putExtra(Extras.USER_ID, id)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}
