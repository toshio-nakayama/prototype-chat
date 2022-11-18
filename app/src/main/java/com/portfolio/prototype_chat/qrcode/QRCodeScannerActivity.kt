package com.portfolio.prototype_chat.qrcode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.journeyapps.barcodescanner.CaptureManager
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivityQrcodeScannerBinding

class QRCodeScannerActivity : AppCompatActivity(),
    QRCodeGeneratorFragment.OnFragmentDestroyListener {

    private lateinit var binding: ActivityQrcodeScannerBinding
    private lateinit var capture: CaptureManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        capture = CaptureManager(this, binding.decoratedBarcodeView)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.decode()
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            val userId: String = it.uid
            binding.myQrcodeButton.setOnClickListener {
                binding.myQrcodeButton.isEnabled = false
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, QRCodeGeneratorFragment.newInstance(userId))
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
        binding.myQrcodeButton.isEnabled = true
        capture.onResume()
    }

}