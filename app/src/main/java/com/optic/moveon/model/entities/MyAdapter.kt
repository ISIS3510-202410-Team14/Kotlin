package com.optic.moveon.model.entities

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val university_list : ArrayList<University>) :RecyclerView.Adapter<MyAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyviewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return university_list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = university_list[position]

        holder.name.text = currentitem.name
        holder.country.text = currentitem.country
        holder.rating.text = currentitem.rating

    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val firstName : TextView = itemView.findViewById(R.id.uni_name)
        val lastName : TextView = itemView.findViewById(R.id.uni_country)
        val age : TextView = itemView.findViewById(R.id.uni_rating)

    }
}