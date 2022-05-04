package com.yarmouk.bainah.ui.fragments.submitNewReport.viewpagerFragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import kotlinx.android.synthetic.main.info_one_fragment.*
import kotlinx.android.synthetic.main.submit_report_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InfoOneFragment:Fragment(R.layout.info_one_fragment) {

    private val mainViewModel: MainViewModel by viewModels(
        {requireParentFragment()}
    )
    private lateinit var uploadImageDialog: AlertDialog
    private lateinit var arrayAdapter:ArrayAdapter<String>
    private var driverLicenseImage = ""
    lateinit var storageReference: StorageReference
    var imageRef = FirebaseStorage.getInstance().reference
    lateinit var progressDialog: ProgressDialog
    var uriList:MutableList<Uri> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageReference = FirebaseStorage.getInstance().getReference("photos")

        progressDialog = ProgressDialog(requireActivity())

        arrayAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.gender))

        uploadImageDialog = SpotsDialog.Builder()
            .setContext(requireContext())
            .setMessage(getString(R.string.uploading_driver_images))
            .setCancelable(false)
            .build()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObservers()

        etGender.setAdapter(arrayAdapter)
        next1.setOnClickListener {
            getUserDataAndNavigate()
        }

        btnUploadDriversLicense.setOnClickListener {
            TedImagePicker.with(requireContext())
                .buttonText(getString(R.string.done))
                .buttonBackground(R.drawable.btn_done)
                .cameraTileBackground(R.color.primaryColor)
                .title(getString(R.string.select_divers))
                .start { uri ->
                     lifecycleScope.launchWhenResumed {
                         uriList.add(uri)
                         delay(500)
                         uploadImagesToFireBase()
                     }
                }
        }

        exitBtn.setOnClickListener {
            showExitDialog()
        }

    }

    //Reference
    //https://firebase.google.com/docs/storage/android/upload-files
    private fun uploadImagesToFireBase() = CoroutineScope(Dispatchers.IO).launch {
        try {
            for (i in 0..uriList.size+1){
                imageRef.child("Photos").child(uriList[i].lastPathSegment.toString()).putFile(uriList[i])
                    .addOnProgressListener {
                    progressDialog.setTitle(getString(R.string.uploading_driver_licence))
                    val progress = (it.bytesTransferred / it.totalByteCount) * 100
                    progressDialog.setMessage("$progress%")
                    progressDialog.show()

                    it.metadata?.reference?.downloadUrl?.addOnCompleteListener {
                        driverLicenseImage = it.result.toString()
                    }

                }.addOnCompleteListener {
                    progressDialog.dismiss()
                    btnUploadDriversLicense.text = getString(R.string.images_uploaded_successfully)
                    btnUploadDriversLicense.setIconResource(R.drawable.ic_check)
                    btnUploadDriversLicense.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green))
                }.addOnFailureListener {
                        progressDialog.dismiss()
                        btnUploadDriversLicense.text = getString(R.string.upload_images_error)
                        btnUploadDriversLicense.setIconResource(R.drawable.cancel_icon)
                        btnUploadDriversLicense.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.red))
                    }

            }

        }catch (e:Exception){
        }
    }

    private fun setUpObservers(){
        mainViewModel.driverLicenseResponse.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    uploadImageDialog.dismiss()
                    showSnackBar("${it.data?.message}",R.color.green)
                    driverLicenseImage = it.data?.imagePath!!
                    btnUploadDriversLicense.text = getString(R.string.images_uploaded_successfully)
                    btnUploadDriversLicense.setIconResource(R.drawable.ic_check)
                    btnUploadDriversLicense.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green))
                }
                Status.ERROR ->{
                    uploadImageDialog.dismiss()
                    showSnackBar("${it.message}",R.color.red)
                    btnUploadDriversLicense.text = getString(R.string.upload_images_error)
                    btnUploadDriversLicense.setIconResource(R.drawable.cancel_icon)
                    btnUploadDriversLicense.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.red))

                }
                Status.LOADING ->{
                    uploadImageDialog.show()
                }
            }
        }

    }

    private fun showExitDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.exitreport))
            .setMessage(getString(R.string.are_you_sure_1))
            .setPositiveButton(getString(R.string.yes)){dialog,_ ->
                dialog.dismiss()
               requireParentFragment().findNavController().popBackStack()
            }
            .setNegativeButton(getString(R.string.no)){dialog,_ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun checkIfNumberHasCharacter(number:String):Boolean{
        val numbers = arrayListOf('0','1','2','3','4','5','6','7','8','9')
        for (i in number){
            if(numbers.contains(i)){
                continue
            }
            else{
                return true
            }
        }
        return false
    }

    private fun checkIfPartLessThanTwo(list:List<String>):Boolean{
        for(i in list){
            if(i.length >= 2){
                continue
            }
            else{
                return true
            }
        }
        return false
    }

    private fun getUserDataAndNavigate(){
        val gender = etGender.text.toString()
        val fullName = etFullName.text.toString()
        val licenseNumber = etLicenseNumber.text.toString()
        val dateOfBirth = etDateOfBirth.text.toString()
        val idNumber = etIDNumber.text.toString()
        val phoneNumber = etPhoneNumber.text.toString()

         if(fullName.split(" ").size != 4){
            etFullName.error = getString(R.string.full_name_error)
        }
        else if(checkIfPartLessThanTwo(fullName.split(" "))){
            etFullName.error = getString(R.string.full_name_error_2)
        }
        else if(licenseNumber.length != 8 || checkIfNumberHasCharacter(licenseNumber)) {
            etLicenseNumber.error = getString(R.string.lisince_number_error)
        }
        else if(dateOfBirth.split("/").size < 3 &&
            dateOfBirth.split("-").size < 3 &&
            dateOfBirth.split("\\").size < 3 ){

            etDateOfBirth.error = getString(R.string.date_error_1) + "\n" + getString(R.string.date_error_2)
        }
        else if(idNumber.length != 10 || checkIfNumberHasCharacter(idNumber)){
            etIDNumber.error = getString(R.string.id_number_error)
        }
        else if(phoneNumber.length != 9 || checkIfNumberHasCharacter(phoneNumber)){
            etPhoneNumber.error = getString(R.string.phone_number_error)
        }
        else if (gender.isEmpty()){
            showSnackBar(getString(R.string.gender_error),R.color.red)
        }
        else if(driverLicenseImage.isEmpty()){
             showSnackBar("Image",R.color.red)
         }
        else{
            mainViewModel.request.mutation {
                it.value?.let { request ->
                    request.fullName = fullName
                    request.licenseNumber = licenseNumber
                    request.dateOfBirth = dateOfBirth
                    request.idNumber = idNumber
                    request.phoneNumber = phoneNumber
                    request.gender = gender
                    request.driverLicense = driverLicenseImage
                }
            }
            requireParentFragment().pager.currentItem = requireParentFragment().pager.currentItem+1
        }
    }

    private fun showSnackBar(message:String,color:Int){
        Snackbar.make(requireView(),message,Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(),color))
            .show()
    }
}