package com.optic.moveon.view


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.optic.moveon.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        // Asumiendo que tienes una referencia a la base de datos de usuarios
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        userRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val name = dataSnapshot.child("name").value.toString()
                val email = dataSnapshot.child("email").value.toString()
                // Asumiendo que tienes campos como 'phone', 'address', etc.
                binding.textName.text = name
                binding.textEmail.text = email



                val imageUrl = dataSnapshot.child("profileImageUrl").value.toString()
                if (imageUrl.isNotEmpty()) {
                    // Cargando la imagen con Picasso
                    Picasso.get().load(imageUrl).into(binding.profileImage)
                }
            } else {
                // Establecer valores predeterminados o manejar la ausencia de datos
                binding.textName.text = "No name provided"
            }
        }.addOnFailureListener {
            // Maneja errores
        }

        binding.buttonEditProfile.setOnClickListener {
            // Iniciar la actividad de edición de perfil
        }
    }

}