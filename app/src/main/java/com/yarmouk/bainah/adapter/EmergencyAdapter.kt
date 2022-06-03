package com.yarmouk.bainah.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yarmouk.bainah.R
import com.yarmouk.bainah.models.EmergencyModel
import kotlinx.android.synthetic.main.row_emergency.view.*

class EmergencyAdapter : RecyclerView.Adapter<EmergencyAdapter.EmergencyViewHolder>() {

    class EmergencyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffUtil = object : DiffUtil.ItemCallback<EmergencyModel>(){
        override fun areItemsTheSame(oldItem: EmergencyModel, newItem: EmergencyModel): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: EmergencyModel, newItem: EmergencyModel): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)

    //To bind the xml design with adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergencyViewHolder {
        return EmergencyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_emergency,parent,false))
    }


    override fun onBindViewHolder(holder: EmergencyViewHolder, position: Int) {
        val emergencyItem = differ.currentList[position]

        holder.itemView.apply {
            title.text = emergencyItem.title
            phone.text = emergencyItem.phoneNumber
            btnCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${emergencyItem.phoneNumber}")
                ContextCompat.startActivity(holder.itemView.context, intent, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}