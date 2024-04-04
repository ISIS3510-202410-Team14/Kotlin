package com.optic.moveon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityLoginBinding
import com.optic.moveon.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val univer1= findViewById<ImageView>(R.id.univer1)
        val univer2= findViewById<ImageView>(R.id.univer2)
        val univer3= findViewById<ImageView>(R.id.univer3)
        val univer4= findViewById<ImageView>(R.id.univer4)


        univer1.setOnClickListener {
            val intent= Intent(this,UniversityActivity3::class.java)
            startActivity(intent)
        }

        univer2.setOnClickListener {
            val intent= Intent(this,UniversityActivity2::class.java)
            startActivity(intent)
        }

        univer3.setOnClickListener {
            val intent= Intent(this,UniversityActivity::class.java)
            startActivity(intent)
        }

        univer4.setOnClickListener {
            val intent= Intent(this,UniversityActivity4::class.java)
            startActivity(intent)
        }

        binding.botonmicro.setOnClickListener {
            val intent = Intent(this, BusquedaVozActivity::class.java)
            startActivity(intent)
        }








    }
}