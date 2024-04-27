package com.optic.moveon.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.optic.moveon.R
import com.optic.moveon.data.UniversityDAO
import com.optic.moveon.databinding.UniInfoItemBinding
import com.optic.moveon.model.FavoritesCache
import com.optic.moveon.model.Imagenes
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.University
import com.optic.moveon.model.entities.UniversityProperties
import com.squareup.picasso.Picasso


class AdapterUniversity(private val university: University, private val context: Context) : RecyclerView.Adapter<AdapterUniversity.MyViewHolder>() {
    private  var firebaseAnalytics: FirebaseAnalytics

    private var databaseReference: DatabaseReference

    init {
        // Inicializar la referencia a la base de datos una sola vez
        databaseReference = FirebaseDatabase.getInstance().getReference("Favorites")
        firebaseAnalytics = FirebaseAnalytics.getInstance(context) //Colocarlo aca esta bien?
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

        val currentUniversity = university

        holder.name.text = currentUniversity.name
        holder.country.text = currentUniversity.country

        // Cargamos la imagen desde el almacenamiento local si está disponible
        val filename = "university_image_${currentUniversity.id}"
        val bitmap = Imagenes.loadImage(context, filename)
        if (bitmap != null) {
            holder.imageView.setImageBitmap(bitmap)
        } else {
            // Si no está en el almacenamiento local, cargamos la imagen desde Firebase
            Picasso.get().load(currentUniversity.image).into(holder.imageView)
        }

        // Verificar si la universidad es favorita para cambiar el icono del favorito
        updateFavoriteIcon(holder.favorite, currentUniversity)

        // Evento de clic en el enlace de la universidad
        holder.icon.setOnClickListener {
            if (!currentUniversity.description.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUniversity.description))

                if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(holder.itemView.context, "No se puede abrir el enlace", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(holder.itemView.context, "La URL es nula o vacía", Toast.LENGTH_SHORT).show()
            }
        }

        holder.form.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity2::class.java)
            intent.putExtra("name",  currentUniversity.name)
            holder.itemView.context.startActivity(intent)
        }








        // Evento de clic en el ícono de favoritos
        holder.favorite.setOnClickListener {
            val uid = UserSessionManager.getUid()  // Obtener el UID del usuario
            if (uid != null) {
                val isFavorite = FavoritesCache.getFavorite(university.id ?: -1) != null
                if (isFavorite) {
                    // Si ya es favorito, remover de favoritos
                    FavoritesCache.removeFavorite(university.id!!)
                    databaseReference.child(uid).child(university.id.toString()).removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Universidad removida de favoritos", Toast.LENGTH_SHORT).show()
                            updateFavoriteIcon(holder.favorite, university, isFavorite = false)
                            universityFirebaseEvent("remove_favorite", university.name)  // Log the event when a university is removed from favorites
                        }
                    }
                } else {
                    // Si no es favorito, agregar a favoritos
                    FavoritesCache.addFavorite(university)
                    databaseReference.child(uid).child(university.id.toString()).setValue(university.name).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Universidad agregada a favoritos", Toast.LENGTH_SHORT).show()
                            updateFavoriteIcon(holder.favorite, university, isFavorite = true)
                            universityFirebaseEvent("add_favorite", university.name)  // Log the event when a university is added to favorites
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

    private fun universityFirebaseEvent(eventName: String, universityName: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, universityName)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
        firebaseAnalytics.logEvent(eventName, bundle)
    }



    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.universityName)
        val country: TextView = itemView.findViewById(R.id.universityLocation)
        val imageView: ImageView = itemView.findViewById(R.id.headerImage)
        val icon: ImageView = itemView.findViewById(R.id.sitio)
        val form: ImageView = itemView.findViewById(R.id.chat)
        val favorite: ImageView = itemView.findViewById(R.id.favorite)
    }
}