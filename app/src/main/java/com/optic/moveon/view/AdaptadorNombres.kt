package com.optic.moveon.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.optic.moveon.R

class AdaptadorNombres(var listaNombres: ArrayList<String>): RecyclerView.Adapter<AdaptadorNombres.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_nombre, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nombre = listaNombres[position]
        holder.tvNombre.text = nombre
    }

    override fun getItemCount(): Int {
        return listaNombres.size
    }

    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
    }

    fun filtrar(listaFiltrada: ArrayList<String>) {
        this.listaNombres = listaFiltrada
        notifyDataSetChanged()
    }
}