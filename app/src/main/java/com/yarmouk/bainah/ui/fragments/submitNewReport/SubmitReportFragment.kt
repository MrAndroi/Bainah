package com.yarmouk.bainah.ui.fragments.submitNewReport

import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yarmouk.bainah.R
import com.yarmouk.bainah.adapter.ViewPagerAdapter
import com.yarmouk.bainah.others.Utility
import com.yarmouk.bainah.ui.fragments.submitNewReport.viewpagerFragments.*
import com.yarmouk.bainah.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.submit_report_fragment.*
import pub.devrel.easypermissions.AppSettingsDialog

@RequiresApi(Build.VERSION_CODES.N)
@AndroidEntryPoint
class SubmitReportFragment:Fragment(R.layout.submit_report_fragment) {

    private val  fragmentList:ArrayList<Fragment> by lazy {
        arrayListOf(
            InfoOneFragment(), InfoTwoFragment(),
            InfoThreeFragment(), InfoFourFragment(), ReportInformationFragment()
        )
    }
    private lateinit var pagerAdapter: ViewPagerAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var onPageChangeCallback: ViewPager2.OnPageChangeCallback
    private lateinit var locationManager: LocationManager

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.isEnabled = false
        onBackPressedCallback.remove()
        pager.unregisterOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        requestLocationPermission()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        infoProgress.max = fragmentList.size
        setUpObservers()

        pagerAdapter = ViewPagerAdapter(childFragmentManager,lifecycle,fragmentList)
        pager.adapter = pagerAdapter
        pager.isUserInputEnabled = false

        onPageChangeCallback = object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                mainViewModel.updateInfoProgress(position+1)

                onBackPressedCallback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        when (position) {
                            in 1..100 ->{
                                goBack(position)
                            }
                            else ->{
                                showExitDialog()
                            }
                        }
                    }
                }
                activity?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
            }
        }
        pager.registerOnPageChangeCallback(onPageChangeCallback)

    }

    private fun setUpObservers(){
        mainViewModel.infoProgress.observe(viewLifecycleOwner){
            infoProgress.setProgressCompat(it,true)
            if(it == 4){
                if(mainViewModel.accidentImagesUploaded.value!!){
                    requireParentFragment()
                        .pager
                        .setCurrentItem(requireParentFragment().pager.currentItem+1,false)
                }
            }
        }
    }

    private fun goBack(position:Int){
        try {
            pager.currentItem = position - 1
        }
        catch (e: java.lang.NullPointerException){
            Log.e("Null Exception",e.message.toString())
        }
    }

    private fun requestLocationPermission(){
        if(Utility.hasLocationPermission(requireContext())){
            return
        }
        else{
            AppSettingsDialog.Builder(requireActivity()).build().show()
            findNavController().popBackStack()
        }
    }

    private fun showExitDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.exitreport))
            .setMessage(getString(R.string.are_you_sure_1))
            .setPositiveButton(getString(R.string.yes)){ dialog, _ ->
                dialog.dismiss()
                requireParentFragment().findNavController().popBackStack()
            }
            .setNegativeButton(getString(R.string.no)){ dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}