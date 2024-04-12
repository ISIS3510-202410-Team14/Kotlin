package com.optic.moveon.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.optic.moveon.R
import com.optic.moveon.databinding.UniInfoItemBinding
import com.optic.moveon.model.entities.University
import com.optic.moveon.model.entities.UniversityProperties

class AdapterUniversity(private val university : University) :RecyclerView.Adapter<AdapterUniversity.MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_item,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 1
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem =university

        holder.name.text = currentitem.name
        holder.country.text = currentitem.country

    }




    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val name : TextView = itemView.findViewById(R.id.universityName)
        val country : TextView = itemView.findViewById(R.id.universityLocation)
    }




}