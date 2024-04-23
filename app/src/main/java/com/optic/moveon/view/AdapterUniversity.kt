package com.optic.moveon.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.optic.moveon.R
import com.optic.moveon.databinding.UniInfoItemBinding
import com.optic.moveon.model.entities.University
import com.optic.moveon.model.entities.UniversityProperties
import com.squareup.picasso.Picasso

class AdapterUniversity(private val university: University, private val context: Context) : RecyclerView.Adapter<AdapterUniversity.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.univer_detail_item,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Se obtiene el objeto University del parámetro university
        val currentitem = university

        // Se establecen los valores en los elementos de la vista del elemento RecyclerView
        holder.name.text = currentitem.name
        holder.country.text = currentitem.country
        Picasso.get().load(currentitem.image).into(holder.imageView)

        holder.icon.setOnClickListener {
            if (!currentitem.description.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentitem.description))

                if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(holder.itemView.context, "No se puede abrir el enlace", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(holder.itemView.context, "La URL es nula o vacía", Toast.LENGTH_SHORT).show()
            }
        }


    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.universityName)
        val country: TextView = itemView.findViewById(R.id.universityLocation)
        val imageView: ImageView = itemView.findViewById(R.id.headerImage)
        val icon: ImageView = itemView.findViewById(R.id.sitio)

    }
}
