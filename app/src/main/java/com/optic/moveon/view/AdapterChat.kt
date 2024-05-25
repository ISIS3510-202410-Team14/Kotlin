package com.optic.moveon.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.optic.moveon.R
import com.optic.moveon.model.entities.Chat

class AdapterChat(private val context: Context, private val chatList: ArrayList<Chat>) : RecyclerView.Adapter<AdapterChat.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_detail_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int = chatList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = chatList[position]
        holder.bind(currentItem)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombre: TextView = itemView.findViewById(R.id.textViewSenderName)
        private val texto: TextView = itemView.findViewById(R.id.textViewMessage)

        fun bind(chat: Chat) {
            nombre.text = chat.name
            texto.text = chat.mensaje
        }
    }
}
