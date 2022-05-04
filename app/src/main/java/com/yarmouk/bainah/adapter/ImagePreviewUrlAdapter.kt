package com.yarmouk.bainah.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.loadAny
import com.yarmouk.bainah.R
import kotlinx.android.synthetic.main.image_preview_item.view.*

class ImagePreviewUrlAdapter: RecyclerView.Adapter<ImagePreviewUrlAdapter.ImagePreviewHolder>() {

    class ImagePreviewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffUtil = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePreviewHolder {
        return ImagePreviewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_preview_item,parent,false))
    }

    override fun onBindViewHolder(holder: ImagePreviewHolder, position: Int) {
        val image = differ.currentList[position]

        holder.itemView.apply {
            imagePreview.loadAny(image){
                placeholder(R.drawable.loading)
                crossfade(true)
                crossfade(500)
                error(R.drawable.image_not_found)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}