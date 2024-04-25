package com.optic.moveon.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.optic.moveon.DefaultApp
import com.optic.moveon.databinding.ActivityUniversityBinding
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.University
import com.optic.moveon.model.entities.UniversityProperties

class UniversityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUniversityBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var adapterUniversity: AdapterUniversity
    private var university: University? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUniversityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = UserSessionManager.getUid()
        Log.d("UniversityActivity", "UID guardado: $uid")

        userRecyclerview = binding.imgdetailed
        userRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        userRecyclerview.setHasFixedSize(true)


        // Inicializar Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        val universityId = intent.getStringExtra("university_name")

        println("jejejeee")
        println(universityId)
        Log.i("UniversityActivity", universityId ?: "University ID is null")
        println(universityId)


        dbref = FirebaseDatabase.getInstance().getReference("Universities")


        val query = dbref.orderByChild("name").equalTo(universityId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firstUniversitySnapshot = snapshot.children.firstOrNull()
                    university = firstUniversitySnapshot?.getValue(University::class.java)
                    initRecyclerView(university)


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

    private fun initRecyclerView(university: University?) {
        userRecyclerview.adapter = AdapterUniversity(university ?: University(),this, (application as DefaultApp).localdb.localUniversityDao())
    }
}
