package com.optic.moveon.view


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.optic.moveon.R
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
                // Define una función local para manejar los valores nulos o vacíos correctamente
                fun getValueOrFallback(value: String?, fallback: String): String {
                    return value?.takeIf { it.isNotBlank() } ?: fallback
                }

                // Usa la función para asignar valores a los campos de texto
                binding.textName.text = getValueOrFallback(dataSnapshot.child("name").value as String?, "No name provided")
                binding.textEmail.text = getValueOrFallback(dataSnapshot.child("email").value as String?, "No email provided")
                binding.textAreaOfStudy.text = getValueOrFallback(dataSnapshot.child("areaOfStudy").value as String?, "No area of study provided")
                binding.textHomeUniversity.text = getValueOrFallback(dataSnapshot.child("homeUniversity").value as String?, "No home university provided")
                binding.textTargetUniversity.text = getValueOrFallback(dataSnapshot.child("targetUniversity").value as String?, "No target university provided")
                binding.textLanguages.text = getValueOrFallback(dataSnapshot.child("languages").value as String?, "No languages specified")

                // Gestión de la imagen de perfil
                val imageUrl = dataSnapshot.child("profileImageUrl").value as String?
                if (!imageUrl.isNullOrEmpty()) {
                    Picasso.get().load(imageUrl).into(binding.profileImage)
                } else {
                    // Establece la imagen predeterminada si no hay URL de imagen
                    Picasso.get().load(R.drawable.persona).into(binding.profileImage)
                }
            } else {
                // Establece valores predeterminados si no existen datos
                binding.textName.text = "No name provided"
                binding.textEmail.text = "No email provided"
                binding.textAreaOfStudy.text = "No area of study provided"
                binding.textHomeUniversity.text = "No home university provided"
                binding.textTargetUniversity.text = "No target university provided"
                binding.textLanguages.text = "No languages specified"
                Picasso.get().load(R.drawable.persona).into(binding.profileImage) // Imagen predeterminada si no hay datos
            }
        }.addOnFailureListener {
            // Maneja errores
        }



        binding.buttonEditProfile.setOnClickListener {
            // Iniciar la actividad de edición de perfil
        }
    }

}