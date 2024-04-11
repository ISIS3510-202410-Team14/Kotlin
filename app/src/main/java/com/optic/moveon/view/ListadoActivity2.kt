package com.optic.moveon.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityListado2Binding
import com.optic.moveon.databinding.ActivityMainBinding
import com.optic.moveon.model.entities.University

class ListadoActivity2 : AppCompatActivity() {

    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var universityList: ArrayList<University>
    private lateinit var binding:ActivityListado2Binding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var mList = mutableListOf<String>()
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("unipictures")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListado2Binding.inflate(layoutInflater)
        setContentView(binding.root)


    }


}






