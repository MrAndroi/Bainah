package com.yarmouk.bainah.ui.fragments.submitNewReport.viewpagerFragments

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.WriterException
import com.yarmouk.bainah.R
import com.yarmouk.bainah.ui.viewModels.MainViewModel
import com.yarmouk.bainah.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.qr_code_fragment.*

@AndroidEntryPoint
class QrCodeFragment:Fragment(R.layout.qr_code_fragment) {

    private val mainViewModel: MainViewModel by viewModels(
        {requireParentFragment()}
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObservers()

        btnFinish.setOnClickListener {
            requireParentFragment().findNavController().popBackStack()
        }
        generateQrCode(arguments?.getString("response") as String)
    }

    private fun setUpObservers(){
        mainViewModel.requestResponse.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    generateQrCode(it.data?.requestID!!)
                }
                Status.ERROR ->{
                    showSnackBar(getString(R.string.genetrate_code_error),R.color.red)
                }
            }
        }
    }

    private fun generateQrCode(requestCode: String) {
        //generate qr code image for elevator code
        val qrEncoder = QRGEncoder(requestCode, null, QRGContents.Type.TEXT, 512)
        qrEncoder.colorBlack = Color.BLACK
        qrEncoder.colorWhite = Color.WHITE

        val bitmap: Bitmap?

        //try to save and upload generated qrCode image and elevator
        try {
            //bitmap to save qrCode image
            bitmap = qrEncoder.bitmap
            requestQrImage.visibility = View.VISIBLE
            requestQrImage.setImageBitmap(bitmap)
        }
        catch (e: WriterException) {
            //catch error for saving the qrImage
            Snackbar.make(requireView(), e.message.toString(), 2000).show()
        }
    }

    private fun showSnackBar(message:String,color:Int){
        Snackbar.make(requireView(),message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(),color))
            .show()
    }
}