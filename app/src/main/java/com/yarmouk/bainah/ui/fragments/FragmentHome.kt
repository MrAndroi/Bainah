package com.yarmouk.bainah.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yarmouk.bainah.R
import com.yarmouk.bainah.adapter.ServiceAdapter
import com.yarmouk.bainah.models.Service
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment.*

@AndroidEntryPoint
class FragmentHome:Fragment(R.layout.home_fragment) {

    //initialize the variables that will be used in this fragment
    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var servicesList:ArrayList<Service>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Handle on back press when the user click back in this page close the app
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

        //Define services in home fragment
        serviceAdapter = ServiceAdapter{serviceName->
            //When statement to handle user click on go button on service item in homepage
            when(serviceName){
                //User click Go in submit report item
                getString(R.string.submit_a_report) ->{
                    findNavController().navigate(R.id.action_fragmentHome_to_submitReportFragment)
                }
                //User click Go in Scan qr code item
                getString(R.string.sca_code) ->{
                    findNavController().navigate(R.id.action_fragmentHome_to_scarQrFragment)
                }
                //User click Go in Emergency numbers item
                getString(R.string.emergent_nums) ->{
                    findNavController().navigate(R.id.action_fragmentHome_to_emergencyFragment)
                }
            }
        }

        //Define empty list
        servicesList = ArrayList()

        //Define first service (Submit report)
        val service1 = Service(
            serviceName = getString(R.string.submit_a_report),
            serviceOverView = getString(R.string.submit_mew),
            serviceBackground = R.drawable.item_two_gradient,
            serviceImage = "https://s8.gifyu.com/images/car-accident-with-drivers-man-woman-vector-illustration_357257-790-removebg-preview.png"
        )

        //Define second service (Scan qr code)
        val service2 = Service(
            getString(R.string.sca_code),
            getString(R.string.submit_mew),
            R.drawable.item_one_gradient,
            "https://s8.gifyu.com/images/pngtree-people-use-smartphone-to-code-scanning-for-payment-and-everything-design-png-image_2158404-removebg-preview.png"
        )

        //Define third service (Emergent numbers)
        val service3 = Service(
            getString(R.string.emergent_nums),
            getString(R.string.submit_mew),
            R.drawable.item_three_gradient,
            "https://s8.gifyu.com/images/5741212-removebg-preview.png"
        )

        //Add services to services list
        servicesList.add(
            service1
        )
        servicesList.add(
            service2
        )
        servicesList.add(
            service3
        )

        //Pass servicesList to services adapter to show it on ui
        serviceAdapter.differ.submitList(servicesList)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Define recyclerview adapter with serviceAdapter
        rvServices.adapter = serviceAdapter

    }

}