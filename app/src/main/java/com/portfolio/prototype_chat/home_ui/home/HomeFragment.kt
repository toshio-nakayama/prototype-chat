package com.portfolio.prototype_chat.home_ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.add_friend.AddFriendActivity
import com.portfolio.prototype_chat.common.Extras
import com.portfolio.prototype_chat.common.NodeNames
import com.portfolio.prototype_chat.databinding.FragmentHomeBinding
import com.portfolio.prototype_chat.profile.ProfileActivity
import com.portfolio.prototype_chat.qrcode.QRCodeScannerActivity
import com.portfolio.prototype_chat.signup.User
import com.portfolio.prototype_chat.util.ToastGenerator

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var dbRootRef: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var dbRefTalk: DatabaseReference
    private var userEventListener: ValueEventListener? = null
    private var talkEventListener: ValueEventListener? = null

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        currentUser = auth.currentUser!!
        dbRootRef = Firebase.database.reference
        dbRefUser = dbRootRef.child(NodeNames.USERS)
        dbRefTalk = dbRootRef.child(NodeNames.TALK)

        setProfile()
        setFriendsCount()
        binding.linearLayoutFriendsList.setOnClickListener { view ->
            view.findNavController().navigate(
                R.id.action_navigation_home_to_navigation_friends
            )
        }
        binding.linearLayoutProfile.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    ProfileActivity::class.java
                )
            )
        }
        binding.floatingActionButtonQrScan.setOnClickListener {
            val options = ScanOptions().apply {
                setOrientationLocked(false)
                setBeepEnabled(false)
                captureActivity = QRCodeScannerActivity::class.java
            }
            barcodeLauncher.launch(options)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        userEventListener = null
        talkEventListener = null
    }

    private fun setProfile() {
        val userId = currentUser.uid
        userEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textViewName.text = currentUser.displayName
                val user = snapshot.getValue(User::class.java)
                user?.statusMessage?.let {
                    binding.textViewStatusMessage.text = user.statusMessage
                }
                Glide.with(requireContext())
                    .load(currentUser.photoUrl)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(binding.imageViewProfile)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        dbRefUser.child(userId).addValueEventListener(userEventListener!!)
    }

    private fun setFriendsCount() {
        val userid = currentUser.uid
        talkEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                binding.textViewFriendsCount.text = count.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        dbRefTalk.child(userid).addValueEventListener(talkEventListener!!)
    }

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> =
        registerForActivityResult(ScanContract()) {
            it.contents?.run {
                val userId: String = it.contents
                validateOnId(userId)
            }

        }

    private fun validateOnId(id: String) {
        val currentUserId = currentUser.uid
        if (id == currentUserId) {
            ToastGenerator.Builder(requireContext()).resId(R.string.invalid_qr_code)
            return
        }
        dbRefTalk.child(currentUserId).child(id).get().addOnSuccessListener { snapshotTalk ->
            if (snapshotTalk.exists()) {
                ToastGenerator.Builder(requireContext()).resId(R.string.allready_registered)
            } else {
                dbRefUser.child(id).get().addOnSuccessListener { snapshotUser ->
                    if (snapshotUser.exists()) {
                        val intent = Intent(activity, AddFriendActivity::class.java)
                        intent.putExtra(Extras.USER_ID, id)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}
