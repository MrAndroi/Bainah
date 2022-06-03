package com.yarmouk.bainah.ui.fragments.submitNewReport.viewpagerFragments

import android.annotation.SuppressLint
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import coil.load
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.yarmouk.bainah.R
import com.yarmouk.bainah.adapter.ImagePreviewAdapter
import com.yarmouk.bainah.ui.viewModels.MainViewModel
import com.yarmouk.bainah.workers.UploadReportWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.report_inforamtion_fragment.*
import kotlinx.android.synthetic.main.submit_report_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

@AndroidEntryPoint
class ReportInformationFragment:Fragment(R.layout.report_inforamtion_fragment) {

    private val mainViewModel: MainViewModel by viewModels(
        {requireParentFragment()}
    )
    private lateinit var imagesAdapter: ImagePreviewAdapter
    private lateinit var imagesPaths:ArrayList<Uri>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagesAdapter = ImagePreviewAdapter{_, _ -> }
        imagesPaths = ArrayList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpFinalImagesRecyclerView()
        setUpObservers()
        setUpEditText()

        confirmBtn.setOnClickListener {
           showConfirmDialog()
        }

    }

    private fun setUpFinalImagesRecyclerView(){
        rvFinalphotos.adapter = imagesAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun setUpObservers(){
        mainViewModel.imagesPath.observe(viewLifecycleOwner){
            imagesPaths = it
            imagesAdapter.differ.submitList(it)
        }

        mainViewModel.request.observe(viewLifecycleOwner){request ->
            //Personal information
            tvFullName.text = "${getString(R.string.full_name_2)} ${request.fullName}"
            tvLicenseNumber.text = "${getString(R.string.licence_number_2)} ${request.licenseNumber}"
            tvDateOfBirth.text = "${getString(R.string.date_of_birth_2)} ${request.dateOfBirth}"
            tvIdNumber.text = "${getString(R.string.id_number_2)} ${request.idNumber}"
            tvGender.text = "${getString(R.string.gender_2)} ${request.gender}"

            //Car License
            ivCarLicense.load(request.carLicense){
                crossfade(true)
                crossfade(500)
            }

            //Accident information
            tvAccidentLocation.text = "${getString(R.string.accident_location_2)} ${getLocationInfo(LatLng(request.latitude, request.longitude))}"
            tvNumberOfCars.text = "${getString(R.string.number_of_cars_2)} ${request.numberOfCars}"
            tvAreaName.text = "${getString(R.string.area_name_2)} ${request.areaName}"
            tvStreetName.text = "${getString(R.string.street_name_2)} ${request.streetName}"

            //Accident time
            val prettyTime = PrettyTime(Locale.getDefault())
            tvAccidentTime.text = "${getString(R.string.accident_time)} ${prettyTime.format(Date(System.currentTimeMillis()))}"

        }
    }

    private fun showSnackBar(message:String,color:Int){
        Snackbar.make(requireView(),message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(),color))
            .show()
    }

    private fun showConfirmDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.submit_report_2))
            .setMessage(getString(R.string.are_you_sure_2))
            .setPositiveButton(getString(R.string.yes)){dialog,_ ->
                dialog.dismiss()
                val imagesList = arrayListOf<String>()
                imagesPaths.forEach {
                    imagesList.add(it.toString())
                }

                val data =  Data.Builder()
                    .putStringArray("list",imagesList.toTypedArray())
                data.putString("report", Gson().toJson(mainViewModel.request.value!!))
                val uploadWorkRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<UploadReportWorker>()
                        .setInputData(data.build())
                        .build()
                WorkManager.getInstance(requireContext()).enqueue(uploadWorkRequest)
                showSnackBar(getString(R.string.uploading_your_request),R.color.green)
                requireParentFragment().findNavController().popBackStack()
            }
            .setNegativeButton(getString(R.string.no)){dialog,_ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setUpEditText(){
        editPersonalInformation.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                delay(100)
                requireParentFragment().pager.setCurrentItem(0,false)
            }
        }

        editCarInformation.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                delay(100)
                requireParentFragment().pager.setCurrentItem(1,false)
            }
        }

        editAccicedntInformation.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                delay(100)
                requireParentFragment().pager.setCurrentItem(2,false)
            }
        }

    }

    private fun getLocationInfo(location: LatLng):String{
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if(addresses.isEmpty()){
            return getString(R.string.unknow_location)
        }

        val street = addresses[0].thoroughfare ?: getString(R.string.unknow_location)
        val state = addresses[0].adminArea ?: getString(R.string.unknow_location)
        val country = addresses[0].countryName ?: getString(R.string.unknow_location)

        return "$country - $state,$street"
    }
}