package com.optic.moveon.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.optic.moveon.model.FavoritesCache
import com.optic.moveon.model.FavoritosResidencia
import com.optic.moveon.model.Imagenes
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.Residence
import com.squareup.picasso.Picasso

class AdapterResidence(private val residence: Residence, private val context: Context) : RecyclerView.Adapter<AdapterResidence.MyViewHolder>() {
    private  var firebaseAnalytics: FirebaseAnalytics
    private var databaseReference: DatabaseReference

    init {
        // Inicializar la referencia a la base de datos una sola vez
        databaseReference = FirebaseDatabase.getInstance().getReference("Favoritos Residencia")
        firebaseAnalytics = FirebaseAnalytics.getInstance(context) //Colocarlo aca esta bien?
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.residence_detail,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentResidence = residence

        holder.name.text = currentResidence.name
        holder.country.text = currentResidence.country

        // Cargamos la imagen desde el almacenamiento local si está disponible
        val filename = "residence_image_${currentResidence.id}"
        val bitmap = Imagenes.loadImage(context, filename)
        if (bitmap != null) {
            holder.imageView.setImageBitmap(bitmap)
        } else {
            // Si no está en el almacenamiento local, cargamos la imagen desde Firebase
            Picasso.get().load(currentResidence.image).into(holder.imageView)
        }

        // Verificar si la universidad es favorita para cambiar el icono del favorito
        updateFavoriteIcon(holder.favorite, currentResidence)

        // Evento de clic en el enlace de la universidad
        holder.icon.setOnClickListener {
            if (!currentResidence.description.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentResidence.description))

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
            intent.putExtra("name",  currentResidence.name)
            holder.itemView.context.startActivity(intent)
        }

        // Evento de clic en el ícono de favoritos
        holder.favorite.setOnClickListener {
            val uid = UserSessionManager.getUid()  // Obtener el UID del usuario
            if (uid != null) {
                val isFavorite = FavoritesCache.getFavorite(residence.id ?: -1) != null
                if (isFavorite) {
                    // Si ya es favorito, remover de favoritos
                    FavoritesCache.removeFavorite(residence.id!!)
                    databaseReference.child(uid).child(residence.id.toString()).removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Residencia removida de favoritos", Toast.LENGTH_SHORT).show()
                            updateFavoriteIcon(holder.favorite, residence, isFavorite = false)
                            residenceFirebaseEvent("remove_favorite", residence.name)  // Log the event when a university is removed from favorites
                        }
                    }
                } else {
                    // Si no es favorito, agregar a favoritos
                    FavoritosResidencia.addFavorite(residence)
                    databaseReference.child(uid).child(residence.id.toString()).setValue(residence.name).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Residencia agregada a favoritos", Toast.LENGTH_SHORT).show()
                            updateFavoriteIcon(holder.favorite, residence, isFavorite = true)
                            residenceFirebaseEvent("add_favorite", residence.name)  // Log the event when a university is added to favorites
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Usuario no identificado", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateFavoriteIcon(favoriteIcon: ImageView, residence: Residence, isFavorite: Boolean? = null) {
        val isCurrentlyFavorite = isFavorite ?: (FavoritesCache.getFavorite(residence.id ?: -1) != null)
        if (isCurrentlyFavorite) {
            favoriteIcon.setImageResource(R.drawable.heartfull)  // Asume que tienes un drawable que representa "favorito"
        } else {
            favoriteIcon.setImageResource(R.drawable.heart) // Asume que tienes un drawable que representa "no favorito"
        }
    }

    private fun residenceFirebaseEvent(eventName: String, residenceName: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, residenceName)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
        firebaseAnalytics.logEvent(eventName, bundle)
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.residenceName)
        val country: TextView = itemView.findViewById(R.id.residenceLocation)
        val imageView: ImageView = itemView.findViewById(R.id.headerImage)
        val icon: ImageView = itemView.findViewById(R.id.sitio)
        val form: ImageView = itemView.findViewById(R.id.chat)
        val favorite: ImageView = itemView.findViewById(R.id.favorite)
    }
}
