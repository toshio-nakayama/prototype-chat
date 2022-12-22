package com.portfolio.prototype_chat.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.journeyapps.barcodescanner.CaptureManager
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivityQrcodeScannerBinding
import com.portfolio.prototype_chat.fragments.QRCodeGeneratorFragment

class QRCodeScannerActivity : AppCompatActivity(),
    QRCodeGeneratorFragment.OnFragmentDestroyListener {

    private lateinit var capture: CaptureManager
    private lateinit var binding: ActivityQrcodeScannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.imagebuttonClose.setOnClickListener { finish() }
        capture = CaptureManager(this, binding.decoratedbarcode)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.decode()
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            val userId: String = it.uid
            binding.buttomMyqrcode.setOnClickListener {
                binding.buttomMyqrcode.isEnabled = false
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentcontainer, QRCodeGeneratorFragment.newInstance(userId))
                    .addToBackStack(null)
                    .commit()
                capture.onPause()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onFragmentDestroy() {
        binding.buttomMyqrcode.isEnabled = true
        capture.onResume()
    }

}