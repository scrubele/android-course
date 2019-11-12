package com.scrubele.scrubeleapp1.adapters

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.scrubele.scrubeleapp1.R
import com.scrubele.scrubeleapp1.models.ProtectedObjectModel
import java.net.URL


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
        DownloadPhoto(holder.photoImageView).execute(dataModel.photo)
    }

    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {
        var nameTextView: TextView = itemLayoutView.findViewById(R.id.object_name)
        var descriptionTextView: TextView = itemLayoutView.findViewById(R.id.object_description)
        var sizeTextView: TextView = itemLayoutView.findViewById(R.id.object_size)
        var photoImageView: ImageView = itemLayoutView.findViewById(R.id.object_photo)
    }

    private class DownloadPhoto(internal val imageView: ImageView) :
        AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            val urlOfPhoto = urls[0]
            return try {
                val inputStream = URL(urlOfPhoto).openStream()
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: Bitmap?) = if (result != null) {
            Toast.makeText(
                imageView.context,
                Resources.getSystem().getString(R.string.successful_photo_downloading),
                Toast.LENGTH_SHORT
            ).show()
            imageView.setImageBitmap(result)
        } else {
            Toast.makeText(
                imageView.context,
                Resources.getSystem().getString(R.string.error_photo_downloading),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}