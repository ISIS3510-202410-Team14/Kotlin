package com.optic.moveon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityLoginBinding
import com.optic.moveon.databinding.ActivityMainBinding
import com.optic.moveon.model.entities.University
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.Chat

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
        getUserData()
        val adapter = MyAdapter(this, universityList)
        userRecyclerview.adapter = adapter

        val uid = UserSessionManager.getUid()
        Log.d("AuthActivity", "UID guardado: $uid")


        setupBottomNavigationView()

        setupBottomChatView()

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
                // Agregar más casos según sea necesario
                else -> false
            }
        }
    }

    private fun setupBottomChatView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_bar)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_perfil -> {
                    val intent = Intent(this, ChatActivity2::class.java)
                    startActivity(intent)
                    true
                }
                // Agregar más casos según sea necesario
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
                        universityList.add(university!!)
                    }
                    userRecyclerview.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}








