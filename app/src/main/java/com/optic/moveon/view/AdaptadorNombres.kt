package com.optic.moveon.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
            if (!isNetworkAvailable(holder.itemView.context)) {
                Toast.makeText(
                    holder.itemView.context,
                    "No hay conexión a internet. No se puede abrir el enlace.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
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

    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}