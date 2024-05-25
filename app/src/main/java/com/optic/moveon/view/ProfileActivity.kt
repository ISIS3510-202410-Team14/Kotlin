package com.optic.moveon.view


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
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
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttonUploadDocs.setOnClickListener {
            val intent = Intent(this, UploadActivity22::class.java)
            startActivity(intent)
        }

        binding.buttonEditProfile.setOnClickListener {
            val bundle = Bundle().apply {
                putString("areaOfStudy", binding.textAreaOfStudy.text.toString().replace("No area of study provided", ""))
                putString("home", binding.textHomeUniversity.text.toString().replace("No home university provided", ""))
                putString("target", binding.textTargetUniversity.text.toString().replace("No target university provided", ""))
                putString("lang", binding.textLanguages.text.toString().replace("No languages specified", ""))
            }
            val intent = Intent(this, EditActivity::class.java).apply {
                putExtras(bundle)
            }
            startActivity(intent)
        }
    }


    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        userId?.let {
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(it)
            fetchUserData(userRef)
        }
    }

    private fun fetchUserData(userRef: DatabaseReference) {
        userRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                updateUIWithUserData(dataSnapshot)
            } else {
                setDefaultUserData()
            }
        }.addOnFailureListener {
            // Maneja errores si es necesario
        }
    }

    private fun updateUIWithUserData(dataSnapshot: DataSnapshot) {
        fun getValueOrFallback(value: String?, fallback: String): String {
            return value?.takeIf { it.isNotBlank() } ?: fallback
        }

        binding.apply {
            textName.text = getValueOrFallback(dataSnapshot.child("name").value as String?, "No name provided")
            textEmail.text = getValueOrFallback(dataSnapshot.child("email").value as String?, "No email provided")
            textAreaOfStudy.text = getValueOrFallback(dataSnapshot.child("areaOfStudy").value as String?, "No area of study provided")
            textHomeUniversity.text = getValueOrFallback(dataSnapshot.child("homeUniversity").value as String?, "No home university provided")
            textTargetUniversity.text = getValueOrFallback(dataSnapshot.child("targetUniversity").value as String?, "No target university provided")
            textLanguages.text = getValueOrFallback(dataSnapshot.child("languages").value as String?, "No languages specified")

            val imageUrl = dataSnapshot.child("profileImageUrl").value as String?
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).into(profileImage)
            } else {
                Picasso.get().load(R.drawable.persona).into(profileImage)
            }
        }
    }

    private fun setDefaultUserData() {
        binding.apply {
            textName.text = "No name provided"
            textEmail.text = "No email provided"
            textAreaOfStudy.text = "No area of study provided"
            textHomeUniversity.text = "No home university provided"
            textTargetUniversity.text = "No target university provided"
            textLanguages.text = "No languages specified"
            Picasso.get().load(R.drawable.persona).into(profileImage)
        }
    }
}

