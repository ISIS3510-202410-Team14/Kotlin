package com.optic.moveon.view

import android.app.AlertDialog
import android.content.DialogInterface
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
import androidx.lifecycle.ViewModelProvider
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
import com.optic.moveon.DefaultApp
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.Chat
import com.optic.moveon.model.entities.LocalUniversity
import com.optic.moveon.viewmodel.MainViewModel
import com.optic.moveon.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var universityList: ArrayList<University>
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, MainViewModelFactory((application as DefaultApp).localdb.localUniversityDao())).get(MainViewModel::class.java)
        userRecyclerview = binding.horizontalScrollView
        userRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        userRecyclerview.setHasFixedSize(true)
        showSimpleAlert()


        universityList = arrayListOf<University>()
        getUserData()
        val adapter = MyAdapter(this, universityList)
        userRecyclerview.adapter = adapter

        //val uid = UserSessionManager.getUid()
        //Log.d("AuthActivity", "UID guardado: $uid")


        setupBottomNavigationView()
        
        binding.botonmicro.setOnClickListener {
            val intent = Intent(this, BusquedaVozActivity::class.java)
            startActivity(intent)
        }


        binding.cerrar.setOnClickListener{
            UserSessionManager.signOut()
            viewModel.deleteUniversities()
            val intent = Intent(this, AuthActivity::class.java)
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

    private fun showSimpleAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Recuerda que en pocos dias se cierran la aplicaciones para Harvard")
        builder.setPositiveButton("Cerrar") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }







    private fun getUserData() {
        viewModel.getUniversities()
        viewModel.universityLiveData.observe(this){
            universityList.clear()
            universityList.addAll(it)
            userRecyclerview.adapter?.notifyDataSetChanged()
        }
    }
}