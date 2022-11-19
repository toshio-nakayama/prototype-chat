package com.portfolio.prototype_chat.qrcode

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.portfolio.prototype_chat.common.Constants
import com.portfolio.prototype_chat.databinding.FragmentQrcodeGeneratorBinding


class QRCodeGeneratorFragment : Fragment() {

    private var param: String? = null
    private var _binding: FragmentQrcodeGeneratorBinding? = null
    private val binding get() = _binding!!
    internal var onDestroyListener: OnFragmentDestroyListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param = it.getString(Constants.ARG_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrcodeGeneratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        param?.let {
            val bitmap = createBitmap(it)
            binding.ivQrcode.setImageBitmap(bitmap)
        }
        binding.ibClose.setOnClickListener {
            parentFragmentManager.popBackStack()
            onDestroyListener?.onFragmentDestroy()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onDestroyListener = context as? OnFragmentDestroyListener
        onDestroyListener ?: throw ClassCastException("$context must implement OnDestroyListener")
    }

    override fun onDetach() {
        super.onDetach()
        onDestroyListener = null
    }

    private fun createBitmap(param: String): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        val result: BitMatrix = multiFormatWriter.encode(param, BarcodeFormat.QR_CODE, 400, 400)
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.createBitmap(result)
    }

    companion object {
        @JvmStatic
        fun newInstance(param: String) =
            QRCodeGeneratorFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.ARG_PARAM, param)
                }
            }
    }

    interface OnFragmentDestroyListener {
        fun onFragmentDestroy()
    }
}