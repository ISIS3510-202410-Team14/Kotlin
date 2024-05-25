package com.optic.moveon.view

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ImageView

import com.squareup.picasso.Picasso
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.optic.moveon.R
import com.optic.moveon.model.entities.Residence

class ResAdapter(private val context: Context, private val residenceList : ArrayList<Residence>) :RecyclerView.Adapter<ResAdapter.MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.res_item,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return residenceList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem =residenceList[position]
        Picasso.get().load(currentitem.image).into(holder.imageView)
        holder.texto.text = currentitem.name


        holder.imageView.setOnClickListener {
            val intent = Intent(holder.itemView.context, UniversityActivity::class.java)
            intent.putExtra("residence_name", currentitem.name)
            println("Residence Name: ${currentitem.name}")
            holder.itemView.context.startActivity(intent)
        }

    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val texto: TextView= itemView.findViewById(R.id.texto)

    }
}
