package com.yarmouk.bainah.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.yarmouk.bainah.R
import com.yarmouk.bainah.adapter.ServiceAdapter
import com.yarmouk.bainah.models.Service
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment.*

@AndroidEntryPoint
class FragmentHome:Fragment(R.layout.home_fragment) {

    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var servicesList:ArrayList<Service>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceAdapter = ServiceAdapter{serviceName->
            when(serviceName){
                getString(R.string.submit_a_report) ->{

                }
                getString(R.string.sca_code) ->{

                }
                getString(R.string.emergent_nums) ->{

                }
            }
        }

        servicesList = ArrayList()
        val service1 = Service(
            serviceName = getString(R.string.submit_a_report),
            serviceOverView = getString(R.string.submit_mew),
            serviceBackground = R.drawable.item_two_gradient,
            serviceImage = "https://s1.gifyu.com/images/removal.ai_tmp-60b49871b080b.png"
        )

        val service2 = Service(
            getString(R.string.sca_code),
            getString(R.string.submit_mew),
            R.drawable.item_one_gradient,
            "https://s3.gifyu.com/images/scan-removebg-preview.png"
        )

        val service3 = Service(
            getString(R.string.emergent_nums),
            getString(R.string.submit_mew),
            R.drawable.item_three_gradient,
            "https://s1.gifyu.com/images/461837-PHE8FQ-316-removebg-preview.png"
        )

        servicesList.add(
            service1
        )
        servicesList.add(
            service2
        )
        servicesList.add(
            service3
        )
        serviceAdapter.differ.submitList(servicesList)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvServices.adapter = serviceAdapter

    }

}