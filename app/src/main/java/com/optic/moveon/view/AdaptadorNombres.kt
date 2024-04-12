package com.optic.moveon.view

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.optic.moveon.R
import java.util.Locale

class AdaptadorNombres(var listaNombresOriginal: ArrayList<String>, var listaUrlsOriginal: ArrayList<String>): RecyclerView.Adapter<AdaptadorNombres.ViewHolder>() {

    var listaNombres = listaNombresOriginal
    var listaUrls = listaUrlsOriginal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_nombre, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nombre = listaNombres[position]
        holder.tvNombre.text = nombre
        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(listaUrls[position]))
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listaNombres.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
    }

    fun filtrar(texto: String) {
        listaNombres = ArrayList()
        listaUrls = ArrayList()

        for (i in listaNombresOriginal.indices) {
            if (listaNombresOriginal[i].toLowerCase(Locale.getDefault()).contains(texto.toLowerCase(Locale.getDefault()))) {
                listaNombres.add(listaNombresOriginal[i])
                listaUrls.add(listaUrlsOriginal[i])
            }
        }
        notifyDataSetChanged()
    }
}