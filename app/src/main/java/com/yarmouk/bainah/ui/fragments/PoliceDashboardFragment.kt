package com.yarmouk.bainah.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yarmouk.bainah.R
import com.yarmouk.bainah.adapter.PoliceReportAdapter
import com.yarmouk.bainah.ui.viewModels.MainViewModel
import com.yarmouk.bainah.util.Status
import com.yarmouk.bainah.util.Status.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.police_dashborad_fragment.*

@AndroidEntryPoint
class PoliceDashboardFragment:Fragment(R.layout.police_dashborad_fragment) {

    private val mainViewModel:MainViewModel by viewModels()
    private lateinit var policeReportAdapter:PoliceReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        policeReportAdapter = PoliceReportAdapter()
        mainViewModel.getAllReports()

        //Handle on back press when the user click back in this page close the app
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setUpObservers()
        refreshLayout.setOnRefreshListener {
            mainViewModel.getAllReports()
        }
    }

    private fun setUpObservers(){
        mainViewModel.reportsResponse.observe(viewLifecycleOwner){
            when(it.status){
                ERROR -> {
                    Toast.makeText(requireContext(),"Error:${it.message}",Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    refreshLayout.isRefreshing = false
                }
                LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    refreshLayout.isRefreshing = true
                }
                SUCCESS -> {
                    policeReportAdapter.differ.submitList(it.data?.results)
                    progressBar.visibility = View.GONE
                    refreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun setUpRecyclerView(){
        recyclerView.adapter = policeReportAdapter
    }

}