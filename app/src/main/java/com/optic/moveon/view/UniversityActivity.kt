package com.optic.moveon.view

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.optic.moveon.DefaultApp
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityUniversityBinding
import com.optic.moveon.model.FavoritesCache
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.LocalUniversity
import com.optic.moveon.model.entities.University
import com.optic.moveon.model.entities.UniversityProperties
import com.optic.moveon.viewmodel.MainViewModel
import com.optic.moveon.viewmodel.MainViewModelFactory
import com.squareup.picasso.Picasso

class UniversityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUniversityBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var adapterUniversity: AdapterUniversity
    private var university: University? = null
    private lateinit var viewModel: MainViewModel
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUniversityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, MainViewModelFactory((application as DefaultApp).localdb.localUniversityDao())).get(MainViewModel::class.java)
        val uid = UserSessionManager.getUid()
        Log.d("UniversityActivity", "UID guardado: $uid")

        userRecyclerview = binding.listDescription
        userRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        userRecyclerview.setHasFixedSize(true)


        // Inicializar Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        val universityId = intent.getStringExtra("university_name")
        println("jejejeee")
        println(universityId)
        Log.i("UniversityActivity", universityId ?: "University ID is null")
        println(universityId)

        binding.favorite.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon(isFavorite)
            viewModel.updateUniversity(LocalUniversity(firebaseId = university?.id, imageUrl = university?.image, favorite = isFavorite))
        }

        dbref = FirebaseDatabase.getInstance().getReference("Universities")


        val query = dbref.orderByChild("name").equalTo(universityId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firstUniversitySnapshot = snapshot.children.firstOrNull()
                    university = firstUniversitySnapshot?.getValue(University::class.java)
                    binding.universityName.text = university?.name ?: ""
                    binding.universityLocation.text = university?.country
                    university?.image?.let {
                        Picasso.get().load(it).into(binding.headerImage)
                    }
                    intFavorite(university)


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

    private fun intFavorite(university: University?) {
        viewModel.getUniversityById(university?.id)
        viewModel.localSingleUniversity.observe(this){
            isFavorite = it.favorite ?: false
            updateFavoriteIcon(it.favorite ?: false)
        }

    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        Log.d("pruebas", "updateFavoriteIcon: $isFavorite")
        if (isFavorite == true) {
            binding.favorite.setImageResource(R.drawable.heartfull)  // Asume que tienes un drawable que representa "favorito"
        } else {
            binding.favorite.setImageResource(R.drawable.heart) // Asume que tienes un drawable que representa "no favorito"
        }
    }

}