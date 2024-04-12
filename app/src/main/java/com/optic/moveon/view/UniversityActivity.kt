package com.optic.moveon.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.optic.moveon.databinding.ActivityUniversityBinding
import com.optic.moveon.model.entities.University
import com.optic.moveon.model.entities.UniversityProperties

class UniversityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUniversityBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var adapterUniversity: AdapterUniversity
    private var university: University? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUniversityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userRecyclerview = binding.imgdetailed
        userRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        userRecyclerview.setHasFixedSize(true)

        // Inicializar Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val universityId = intent.getStringExtra("university_id")

        val databaseReference = FirebaseDatabase.getInstance().getReference("Universities")

        val query = databaseReference.orderByChild("id").equalTo(universityId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firstUniversitySnapshot = snapshot.children.firstOrNull()
                    university = firstUniversitySnapshot?.getValue(University::class.java)
                    initRecyclerView()
                } else {
                    // Si no se encuentra ninguna universidad con el ID proporcionado, mostrar un mensaje de error
                    // (aquí puedes manejar el caso de que la universidad no se encuentre)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores de base de datos, si es necesario
                // (aquí puedes manejar los errores de Firebase)
            }
        })
    }

    private fun initRecyclerView() {
        adapterUniversity = AdapterUniversity(university ?: University())
    }
}
