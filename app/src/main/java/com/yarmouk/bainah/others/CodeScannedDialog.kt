package com.yarmouk.bainah.others

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.yarmouk.bainah.R
import com.yarmouk.bainah.models.Participant
import com.yarmouk.bainah.ui.viewModels.MainViewModel
import com.yarmouk.bainah.util.Status
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.code_scaned_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CodeScannedDialog:DialogFragment(R.layout.code_scaned_dialog) {

    private val mainViewModel: MainViewModel by viewModels(
        {requireParentFragment()}
    )
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var addingParticipantDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.gender))

        addingParticipantDialog = SpotsDialog.Builder()
            .setContext(requireContext())
            .setMessage(getString(R.string.adding_new_particioant))
            .setCancelable(false)
            .build()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObservers()
        tvDialogOk.setOnClickListener {
            getUserDataAndNavigate()
        }
        etGender.setAdapter(arrayAdapter)

    }

    private fun setUpObservers(){
        mainViewModel.participantResponse.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        addingParticipantDialog.dismiss()
                        dialog?.dismiss()
                        DoneDialog()
                            .show(requireParentFragment().childFragmentManager,"doneDialog")
                    }
                }
                Status.ERROR -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        showSnackBar(getString(R.string.adding_paritcipant_error),R.color.red)
                        dialog?.dismiss()
                        addingParticipantDialog.dismiss()
                    }
                }
                Status.LOADING -> {
                    addingParticipantDialog.show()
                }
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
        else{
            val participant = Participant(
                fullName = fullName,
                licenseNumber = licenseNumber,
                dateOfBirth = dateOfBirth,
                idNumber = idNumber,
                phoneNumber = phoneNumber,
                gender = gender,
            )
            mainViewModel
                .addNewParticipant(mainViewModel.requestGetResponse.value?.data?.request?.requestId!!,participant)
        }
    }

    private fun showSnackBar(message:String,color:Int){
        Snackbar.make(requireView(),message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(),color))
            .show()
    }

}