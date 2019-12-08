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


class DataAdapter(
    private var dataList: List<ProtectedObjectModel>,
    private val itemClickListener: (ProtectedObjectModel) -> Unit
) :

    RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], itemClickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nameTextView: TextView = itemView.findViewById(R.id.object_name)
        private var descriptionTextView: TextView = itemView.findViewById(R.id.object_description)
        private var sizeTextView: TextView = itemView.findViewById(R.id.object_size)
        private var photoImageView: ImageView = itemView.findViewById(R.id.object_photo)

        fun bind(dataModel: ProtectedObjectModel, clickListener: (ProtectedObjectModel) -> Unit) {
            nameTextView.text = dataModel.name
            descriptionTextView.text = dataModel.description
            sizeTextView.text = dataModel.size
            Picasso
                .get()
                .load(dataModel.photo)
                .into(photoImageView)

            itemView.setOnClickListener { clickListener(dataModel) }
        }
    }


}