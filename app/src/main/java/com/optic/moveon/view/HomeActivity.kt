package com.optic.moveon.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.optic.moveon.R
import com.optic.moveon.model.entities.University
import com.optic.moveon.model.entities.Residence

class HomeActivity : AppCompatActivity() {

    private lateinit var dbref: DatabaseReference
    private lateinit var dbrefres: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var residenceRecyclerview: RecyclerView
    private lateinit var universityList: ArrayList<University>
    private lateinit var residenceList: ArrayList<Residence>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        userRecyclerview = findViewById(R.id.universityList)
        residenceRecyclerview = findViewById(R.id.residenceList)

        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)

        residenceRecyclerview.layoutManager = LinearLayoutManager(this)
        residenceRecyclerview.setHasFixedSize(true)

        universityList = arrayListOf<University>()
        residenceList = arrayListOf<Residence>()

        dbref = FirebaseDatabase.getInstance().getReference("Universities")
        dbrefres = FirebaseDatabase.getInstance().getReference("Residences")

    }


}