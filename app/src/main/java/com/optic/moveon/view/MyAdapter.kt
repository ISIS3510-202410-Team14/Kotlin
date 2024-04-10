package com.optic.moveon.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.optic.moveon.R
import com.optic.moveon.model.entities.University

class MyAdapter(private val universityList : ArrayList<University>) :RecyclerView.Adapter<MyAdapter.MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_item,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return universityList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem =universityList[position]

        holder.name.text = currentitem.name
        holder.country.text = currentitem.country
        holder.description.text = currentitem.description

    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val name : TextView = itemView.findViewById(R.id.uni_name)
        val country : TextView = itemView.findViewById(R.id.uni_country)
        val description : TextView = itemView.findViewById(R.id.uni_rating)
    }
}