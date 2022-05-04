package com.yarmouk.bainah.others

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.yarmouk.bainah.R
import com.yarmouk.bainah.adapter.ImagePreviewUrlAdapter
import com.yarmouk.bainah.models.RequestGet
import com.yarmouk.bainah.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.confirm_request_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConfirmRequestDialog:DialogFragment(R.layout.confirm_request_dialog) {

    private val mainViewModel: MainViewModel by viewModels(
        {requireParentFragment()}
    )

    private lateinit var imagesAdapter: ImagePreviewUrlAdapter
    private lateinit var pleaseWaitDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagesAdapter = ImagePreviewUrlAdapter()

        pleaseWaitDialog = SpotsDialog.Builder()
            .setContext(requireContext())
            .setMessage(getString(R.string.please_wait))
            .setCancelable(false)
            .build()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvConfirmImages.setHasFixedSize(false)
        rvConfirmImages.adapter = imagesAdapter

        getRequestData()

    }

    private fun getRequestData(){
        mainViewModel.requestGetResponse.value?.data?.let {response ->
            setUpViews(response.request)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpViews(request: RequestGet){
        imagesAdapter.differ.submitList(request.accidentImages.toMutableList())
        tvReporterName.text = getString(R.string.reporter_name) + request.fullName
        tvConfirmAreaName.text = getString(R.string.area_name_2) + request.areaName
        tvConfirmStreetName.text = getString(R.string.street_name_2) + request.streetName
        tvConfirmNumberOfCars.text = getString(R.string.nunber_of_cars_2) + request.numberOfCars
        tvDeclineAccidentInfo.setOnClickListener {
            dialog?.dismiss()
        }
        tvConfirmAccidentInfo.setOnClickListener {
           CoroutineScope(Dispatchers.Main).launch {
               pleaseWaitDialog.show()
               delay(1000)
               dialog?.dismiss()
               pleaseWaitDialog.dismiss()
               CodeScannedDialog()
                   .apply {
                       isCancelable = false
                   }
                   .show(requireParentFragment().childFragmentManager,"codeScannedDialog")
           }
        }
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null)
        {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog?.window?.setLayout(width, height)
        }
    }
}