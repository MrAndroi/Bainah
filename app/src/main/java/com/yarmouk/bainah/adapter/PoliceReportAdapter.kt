package com.yarmouk.bainah.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.yarmouk.bainah.R
import com.yarmouk.bainah.models.ReportMeta
import kotlinx.android.synthetic.main.row_report_item.view.*


class PoliceReportAdapter : RecyclerView.Adapter<PoliceReportAdapter.PoliceReportViewHolder>() {

    class PoliceReportViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffUtil = object : DiffUtil.ItemCallback<ReportMeta>(){
        override fun areItemsTheSame(oldItem: ReportMeta, newItem: ReportMeta): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: ReportMeta, newItem: ReportMeta): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)

    //To bind the xml design with adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoliceReportViewHolder {
        return PoliceReportViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_report_item,parent,false))
    }


    override fun onBindViewHolder(holder: PoliceReportViewHolder, position: Int) {
        val report = differ.currentList[position]

        holder.itemView.apply {
            reporterNameTextView.text = report.fullName
            reporterPhoneTextView.text = report.phoneNumber
            reporterIdImageView.load(report.driverLicense)
            callReportButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${report.phoneNumber}")
                startActivity(holder.itemView.context,intent,null)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}