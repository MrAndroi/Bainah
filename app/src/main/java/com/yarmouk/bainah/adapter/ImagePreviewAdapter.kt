package com.yarmouk.bainah.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.loadAny
import com.yarmouk.bainah.R
import kotlinx.android.synthetic.main.image_preview_item.view.*

class ImagePreviewAdapter(private val onClick:(image:Uri,pos:Int) -> Unit): RecyclerView.Adapter<ImagePreviewAdapter.ImagePreviewHolder>() {

    class ImagePreviewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffUtil = object : DiffUtil.ItemCallback<Uri>(){
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
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
                error(R.drawable.warning)
            }
            setOnLongClickListener {
                onClick(image,position)
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}