package com.yarmouk.bainah.ui.fragments.submitNewReport.viewpagerFragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yarmouk.bainah.R
import com.yarmouk.bainah.ui.viewModels.MainViewModel
import com.yarmouk.bainah.util.Status
import com.yarmouk.bainah.util.mutation
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.android.synthetic.main.info_two_fragment.*
import kotlinx.android.synthetic.main.submit_report_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InfoTwoFragment:Fragment(R.layout.info_two_fragment) {

    private val mainViewModel: MainViewModel by viewModels(
        {requireParentFragment()}
    )
    private lateinit var uploadImageDialog: AlertDialog
    var carLicenseImage = ""
    lateinit var storageReference: StorageReference
    var imageRef = FirebaseStorage.getInstance().reference
    lateinit var progressDialog: ProgressDialog
    var uriList:MutableList<Uri> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageReference = FirebaseStorage.getInstance().getReference("photos")

        progressDialog = ProgressDialog(requireActivity())

        uploadImageDialog = SpotsDialog.Builder()
            .setContext(requireContext())
            .setMessage(getString(R.string.uploading_car_lic_image))
            .setCancelable(false)
            .build()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObservers()

        next2.setOnClickListener {
            val carNumber1 = etCarNumber1.text.toString()
            val carNumber2 = etCarNumber2.text.toString()
            if(carLicenseImage.isEmpty()){
                showSnackBar(getString(R.string.upload_car_lic_error),R.color.red)
            }
            else if(carNumber1.isBlank()){
                etCarNumber1.error = getString(R.string.car_code_error)
            }
            else if(carNumber2.isBlank()){
                etCarNumber2.error = getString(R.string.car_number_error)
            }
            else{
                mainViewModel.request.mutation {
                    it.value?.carLicense = carLicenseImage
                }
                requireParentFragment().pager.currentItem = requireParentFragment().pager.currentItem+1
            }

        }

        back1.setOnClickListener {
            requireParentFragment().pager.currentItem = requireParentFragment().pager.currentItem-1
        }

        btnUploadCarsLicense.setOnClickListener {
            TedImagePicker.with(requireContext())
                .buttonText(getString(R.string.done))
                .buttonBackground(R.drawable.btn_done)
                .cameraTileBackground(R.color.primaryColor)
                .title(getString(R.string.please_select_cars))
                .start { uri ->
                    val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
                    ivCarLincenes.setImageBitmap(bitmap)
                    lifecycleScope.launchWhenResumed {
                        uriList.add(uri)
                        delay(500)
                        uploadImagesToFireBase()
                    }

                }
        }

    }

    //Reference
    //https://firebase.google.com/docs/storage/android/upload-files
    private fun uploadImagesToFireBase() = CoroutineScope(Dispatchers.IO).launch {
        try {
            for (i in 0..uriList.size+1){
                imageRef.child("Photos").child(uriList[i].lastPathSegment.toString()).putFile(uriList[i]).addOnProgressListener {
                    progressDialog.setTitle(getString(R.string.uploading_car_licence))
                    val progress = (it.bytesTransferred / it.totalByteCount) * 100
                    progressDialog.setMessage("$progress%")
                    progressDialog.show()

                    it.metadata?.reference?.downloadUrl?.addOnCompleteListener {
                        carLicenseImage = it.result.toString()
                    }

                }.addOnCompleteListener {
                    progressDialog.dismiss()
                    btnUploadCarsLicense.text = getString(R.string.images_uploaded_successfully)
                    btnUploadCarsLicense.setIconResource(R.drawable.ic_check)
                    btnUploadCarsLicense.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green))
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    btnUploadCarsLicense.text = getString(R.string.upload_images_error)
                    btnUploadCarsLicense.setIconResource(R.drawable.cancel_icon)
                    btnUploadCarsLicense.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.red))
                }

            }

        }catch (e:Exception){
        }
    }

    private fun setUpObservers(){
       mainViewModel.carLicenseResponse.observe(viewLifecycleOwner){
           when(it.status){
               Status.SUCCESS ->{
                   uploadImageDialog.dismiss()
                   showSnackBar("${it.data?.message}",R.color.green)
                   carLicenseImage = it.data?.imagePath!!
                   btnUploadCarsLicense.text = getString(R.string.images_uploaded_successfully)
                   btnUploadCarsLicense.setIconResource(R.drawable.ic_check)
                   btnUploadCarsLicense.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green))
               }
               Status.LOADING ->{
                   uploadImageDialog.show()
               }
               Status.ERROR ->{
                   uploadImageDialog.dismiss()
                   showSnackBar("${it.data?.message}",R.color.red)
                   btnUploadCarsLicense.text = getString(R.string.upload_images_error)
                   btnUploadCarsLicense.setIconResource(R.drawable.cancel_icon)
                   btnUploadCarsLicense.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.red))
               }
           }
       }
    }

    private fun showSnackBar(message:String,color:Int){
        Snackbar.make(requireView(),message,Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(),color))
            .show()
    }

}