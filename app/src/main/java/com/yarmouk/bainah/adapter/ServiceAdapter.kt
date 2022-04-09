package com.yarmouk.bainah.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.yarmouk.bainah.R
import com.yarmouk.bainah.models.Service
import kotlinx.android.synthetic.main.service_item.view.*

class ServiceAdapter(private val onClick:(serviceName:String) -> Unit):RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    private val diffUtil = object : DiffUtil.ItemCallback<Service>(){
        override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)

    //To bind the xml design with adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        return ServiceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.service_item,parent,false))
    }


    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = differ.currentList[position]

        holder.itemView.apply {
            tvServiceName.text = service.serviceName
            tvServiceOverView.text = service.serviceOverView
            serviceBackground.setBackgroundResource(service.serviceBackground)
            serviceImage.load(service.serviceImage){
                placeholder(R.drawable.warning)
                error(R.drawable.warning)
            }
            btnOpenService.setOnClickListener {
                onClick(service.serviceName)
            }
        }
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }


}