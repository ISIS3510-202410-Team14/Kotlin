package com.optic.moveon.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityEditBinding
import com.optic.moveon.model.entities.User

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var dialog: Dialog
    private var imageUrl: String? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 71
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        storageReference = FirebaseStorage.getInstance().reference

        binding.buttonUpdate.setOnClickListener {
            updateUserInfo()
        }

        binding.buttonChooseImage.setOnClickListener {
            chooseImage()
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            filePath?.let {
                binding.imageView.setImageURI(it)
                uploadImage(it)
            }
        }
    }

    private fun uploadImage(filePath: Uri) {
        val ref = storageReference.child("images/${firebaseAuth.currentUser?.uid}")
        ref.putFile(filePath)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    imageUrl = uri.toString()
                    Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        val areaOfStudy = binding.areaOfStudy.text.toString().trim()
        val homeUniversity = binding.homeUniversity.text.toString().trim()
        val targetUniversity = binding.targetUniversity.text.toString().trim()
        val languages = binding.languages.text.toString().trim()

        val userUpdates = hashMapOf<String, Any>(
            "areaOfStudy" to areaOfStudy,
            "homeUniversity" to homeUniversity,
            "targetUniversity" to targetUniversity,
            "languages" to languages
        )

        imageUrl?.let {
            userUpdates["profileImageUrl"] = it
        }

        databaseReference.child(firebaseAuth.currentUser?.uid!!).updateChildren(userUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User Info Updated Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to Update User Info", Toast.LENGTH_SHORT).show()
                }
            }
    }
}