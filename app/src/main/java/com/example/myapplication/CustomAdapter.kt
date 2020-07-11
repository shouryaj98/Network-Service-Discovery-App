package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val myDataset: ArrayList<ServiceModel>) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    class MyViewHolder(val itemVie: View) : RecyclerView.ViewHolder(itemVie){

        fun bindItems(serviceModel: ServiceModel){
            val textViewName = itemVie.findViewById(R.id.name) as TextView
            val textViewType = itemVie.findViewById(R.id.type) as TextView
            val textViewIP = itemVie.findViewById(R.id.ip) as TextView
            val textViewPort = itemVie.findViewById(R.id.port) as TextView

            textViewName.text = "Service Name: ${serviceModel.name}"
            textViewType.text = "Service Type: ${serviceModel.type}"
            textViewIP.text = "IP Address: ${serviceModel.ip}"
            textViewPort.text = "Port: ${serviceModel.port}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_text_view, parent, false)
        return MyViewHolder(textView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(myDataset[position])
    }

    override fun getItemCount() = myDataset.size

}
