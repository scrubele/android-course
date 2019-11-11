package com.scrubele.scrubeleapp1.adapters

//import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.scrubele.scrubeleapp1.R
import com.scrubele.scrubeleapp1.models.ProtectedObjectModel


class DataAdapter(private var dataList: List<ProtectedObjectModel>) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_home, parent, false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel= dataList[position]
        holder.nameTextView.text=dataModel.name
        holder.descriptionTextView.text=dataModel.description
        holder.sizeTextView.text=dataModel.size
//        holder.robotsTextView.con=dataModel.robots
    }


    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {
        var nameTextView:TextView = itemLayoutView.findViewById(R.id.object_name)
        var descriptionTextView:TextView = itemLayoutView.findViewById(R.id.object_description)
        var sizeTextView:TextView = itemLayoutView.findViewById(R.id.object_size)
//        var robotsTextView:ListView = itemLayoutView.findViewById(R.id.robots)


    }


}


