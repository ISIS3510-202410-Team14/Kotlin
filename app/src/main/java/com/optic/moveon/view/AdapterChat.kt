package com.optic.moveon.view

import android.content.Context
import android.content.Intent
import com.squareup.picasso.Picasso
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.optic.moveon.R
import com.optic.moveon.model.entities.Chat

class AdapterChat(private val context: Context, private val chatList : ArrayList<Chat>) :RecyclerView.Adapter<AdapterChat.MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.chat_detail_item,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem =chatList[position]
        holder.nombre.text = currentitem.name
        holder.texto.text = currentitem.mensaje

    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val nombre: TextView= itemView.findViewById(R.id.textViewSenderName)
        val texto: TextView= itemView.findViewById(R.id.textViewMessage)
    }


}