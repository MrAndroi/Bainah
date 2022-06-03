package com.yarmouk.bainah.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.yarmouk.bainah.R
import com.yarmouk.bainah.adapter.EmergencyAdapter
import com.yarmouk.bainah.models.EmergencyModel
import kotlinx.android.synthetic.main.fragment_emergancy.*

class EmergencyFragment:Fragment(R.layout.fragment_emergancy) {

    private lateinit var emergencyAdapter: EmergencyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emergencyAdapter = EmergencyAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = emergencyAdapter
        emergencyAdapter.differ.submitList(getDate())
    }

    private fun getDate():List<EmergencyModel>{
        val e1 = EmergencyModel("الدفاع المدني","911")
        val e2 = EmergencyModel("الامن العام","196")
        val e3 = EmergencyModel("ادارة التنفيذ القضائي","117111")
        return listOf(e1,e2,e3)
    }
}