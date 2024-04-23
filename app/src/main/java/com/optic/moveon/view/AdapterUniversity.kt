package com.optic.moveon.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.optic.moveon.R
import com.optic.moveon.databinding.UniInfoItemBinding
import com.optic.moveon.model.FavoritesCache
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.University
import com.optic.moveon.model.entities.UniversityProperties
import com.squareup.picasso.Picasso

class AdapterUniversity(private val university: University, private val context: Context) : RecyclerView.Adapter<AdapterUniversity.MyViewHolder>() {

    private lateinit var databaseReference: DatabaseReference

    init {
        // Inicializar la referencia a la base de datos una sola vez
        databaseReference = FirebaseDatabase.getInstance().getReference("Favorites")
    }

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
        // Intenta obtener la universidad del cache primero
        val cachedUniversity = FavoritesCache.getFavorite(university.id ?: -1)
        val currentitem = cachedUniversity ?: university

        // Se establecen los valores en los elementos de la vista del elemento RecyclerView
        holder.name.text = currentitem.name
        holder.country.text = currentitem.country
        Picasso.get().load(currentitem.image).into(holder.imageView)

        // Verificar si la universidad es favorita para cambiar el icono del favorito
        updateFavoriteIcon(holder.favorite, currentitem)

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

        holder.favorite.setOnClickListener {
            val uid = UserSessionManager.getUid()  // Obtener el UID del usuario
            if (uid != null) {
                if (FavoritesCache.getFavorite(university.id ?: -1) != null) {
                    // Si ya es favorito, remover de favoritos
                    FavoritesCache.removeFavorite(university.id!!)
                    databaseReference.child(uid).child(university.id.toString()).removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Universidad removida de favoritos", Toast.LENGTH_SHORT).show()
                            updateFavoriteIcon(holder.favorite, currentitem, isFavorite = false)
                        }
                    }
                } else {
                    // Si no es favorito, agregar a favoritos
                    FavoritesCache.addFavorite(currentitem)
                    databaseReference.child(uid).child(university.id.toString()).setValue(university.name).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Universidad agregada a favoritos", Toast.LENGTH_SHORT).show()
                            updateFavoriteIcon(holder.favorite, currentitem, isFavorite = true)
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Usuario no identificado", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun updateFavoriteIcon(favoriteIcon: ImageView, university: University, isFavorite: Boolean? = null) {
        val isCurrentlyFavorite = isFavorite ?: (FavoritesCache.getFavorite(university.id ?: -1) != null)
        if (isCurrentlyFavorite) {
            favoriteIcon.setImageResource(R.drawable.heartfull)  // Asume que tienes un drawable que representa "favorito"
        } else {
            favoriteIcon.setImageResource(R.drawable.heart) // Asume que tienes un drawable que representa "no favorito"
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.universityName)
        val country: TextView = itemView.findViewById(R.id.universityLocation)
        val imageView: ImageView = itemView.findViewById(R.id.headerImage)
        val icon: ImageView = itemView.findViewById(R.id.sitio)
        val favorite: ImageView = itemView.findViewById(R.id.favorite)
    }
}
