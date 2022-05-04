package com.yarmouk.bainah.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.budiyev.android.codescanner.*
import com.google.android.material.snackbar.Snackbar
import com.yarmouk.bainah.R
import com.yarmouk.bainah.others.ConfirmRequestDialog
import com.yarmouk.bainah.ui.viewModels.MainViewModel
import com.yarmouk.bainah.util.Status
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.scan_qr_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScarQrFragment:Fragment(R.layout.scan_qr_fragment) {

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var codeScanner: CodeScanner
    private lateinit var scanDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scanDialog = SpotsDialog.Builder()
            .setContext(requireContext())
            .setMessage(getString(R.string.code_scanned))
            .setCancelable(false)
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObservers()
        codeScanner = CodeScanner(requireActivity(), scanner_view)
        getCameraPermission()

    }

    //function to get camera permission
    private fun getCameraPermission(){

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 500)
        }
        else{
            initQrCodeScanner()
        }
    }

    //function to set up the code scanner settings
    private fun initQrCodeScanner(){
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,


        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.CONTINUOUS // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        //called when the code scanned successfully
        codeScanner.decodeCallback = DecodeCallback {result ->

            CoroutineScope(Dispatchers.Main).launch {
                //clean scanner resource
                CoroutineScope(Dispatchers.Main).launch {
                    codeScanner.releaseResources()
                    mainViewModel.getRequest(result.text)
                }
            }

        }
        //called when there is error in the scan
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            CoroutineScope(Dispatchers.Main).launch {
                Snackbar.make(requireView(), "Camera initialization error: ${it.message}", 2000).show()
            }
        }

        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun setUpObservers(){
        mainViewModel.requestGetResponse.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        scanDialog.dismiss()
                        ConfirmRequestDialog().apply {
                            isCancelable = false
                        }
                            .show(childFragmentManager,"confirmDialog")
                    }
                }
                Status.ERROR ->{
                    scanDialog.dismiss()
                    showSnackBar(getString(R.string.code_scan_error),R.color.red)
                }
                Status.LOADING ->{
                    scanDialog.show()
                }
            }
        }
    }

    private fun showSnackBar(message:String,color:Int){
        Snackbar.make(requireView(),message,Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(),color))
            .show()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


}