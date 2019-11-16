package com.scrubele.scrubeleapp1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.scrubele.scrubeleapp1.R
import com.scrubele.scrubeleapp1.models.ProtectedObjectModel
import com.squareup.picasso.Picasso


class DataAdapter(private var dataList: List<ProtectedObjectModel>) :

    RecyclerView.Adapter<DataAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_home,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = dataList[position]
        holder.nameTextView.text = dataModel.name
        holder.descriptionTextView.text = dataModel.description
        holder.sizeTextView.text = dataModel.size
//        DownloadPhoto(holder.photoImageView).execute(dataModel.photo)
        Picasso
            .get()
            .load(dataModel.photo)
            .into(holder.photoImageView)
    }

    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {
        var nameTextView: TextView = itemLayoutView.findViewById(R.id.object_name)
        var descriptionTextView: TextView = itemLayoutView.findViewById(R.id.object_description)
        var sizeTextView: TextView = itemLayoutView.findViewById(R.id.object_size)
        var photoImageView: ImageView = itemLayoutView.findViewById(R.id.object_photo)
    }

}