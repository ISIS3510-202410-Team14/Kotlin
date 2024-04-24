package com.optic.moveon.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityMainBinding
import com.optic.moveon.model.Imagenes
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.University
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var universityList: ArrayList<University>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userRecyclerview = binding.horizontalScrollView
        userRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        userRecyclerview.setHasFixedSize(true)

        universityList = arrayListOf<University>()
        val adapter = MyAdapter(this, universityList)
        userRecyclerview.adapter = adapter

        val uid = UserSessionManager.getUid()
        Log.d("AuthActivity", "UID guardado: $uid")

        getUserData()
        setupBottomNavigationView()
        binding.botonmicro.setOnClickListener {
            val intent = Intent(this, BusquedaVozActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_bar)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_location -> {
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference("Universities")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val university = userSnapshot.getValue(University::class.java)
                        // Guardar imagen localmente si es necesario
                        university?.let {
                            // Verificar si la imagen ya está guardada localmente antes de intentar guardarla nuevamente
                            if (Imagenes.loadImage(this@MainActivity, "university_image_${university.id}") == null) {
                                saveUniversityImageLocally(it)
                            }
                            universityList.add(university)
                        }
                    }
                    userRecyclerview.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores de cancelación, si es necesario
            }
        })
    }

    private fun saveUniversityImageLocally(university: University) {
        val imageUrl = university.image
        if (!imageUrl.isNullOrEmpty()) {
            val filename = "university_image_${university.id}"
            // Verificar si la imagen ya está en caché
            if (Imagenes.loadImage(this, filename) == null) {
                // Si la imagen no está en caché, cargarla desde la URL y guardarla en caché
                Picasso.get().load(imageUrl).into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        bitmap?.let {
                            Imagenes.saveImage(this@MainActivity, filename, it)
                            Log.d("MainActivity", "Imagen guardada en caché: $filename")
                        }
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        // Manejar fallos en la carga de la imagen
                        Log.e("MainActivity", "Error cargando la imagen: ${e?.message}")
                        // Aquí puedes mostrar un mensaje de error al usuario
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        // Aquí puedes realizar alguna acción mientras la imagen está siendo cargada
                    }
                })
            } else {
                Log.d("MainActivity", "La imagen ya está en caché: $filename")
            }
        }
    }
}










