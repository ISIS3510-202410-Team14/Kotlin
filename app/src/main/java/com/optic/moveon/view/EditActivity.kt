package com.optic.moveon.view

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
    private var imageUri: Uri? = null
    private lateinit var dialog: Dialog
    private var imageUrl: String? = null
    private lateinit var networkReceiver: BroadcastReceiver
    private lateinit var sharedPreferences: SharedPreferences

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
        sharedPreferences = getSharedPreferences("UserCache", Context.MODE_PRIVATE)
        initNetworkReceiver()

        val bundle = intent.extras
        bundle?.let {  // Asegurarse de que el Bundle no sea nulo
            binding.areaOfStudy.setText(it.getString("areaOfStudy"))
            binding.homeUniversity.setText(it.getString("home"))
            binding.targetUniversity.setText(it.getString("target"))
            binding.languages.setText(it.getString("lang"))
        }
        binding.buttonUpdate.setOnClickListener {
            if (isNetworkAvailable(this)) {
                updateUserInfo()
            } else {
                cacheUserData()
                Toast.makeText(this, "No internet connection. Your changes have been cached and will be processed once connectivity is restored.", Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonChooseImage.setOnClickListener {
            chooseImage()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    private fun initNetworkReceiver() {
        networkReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceive(context: Context?, intent: Intent?) {
                if (isNetworkAvailable(context!!)) {
                    retrieveCachedDataAndUpload()
                }
            }
        }
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }

    private fun cacheUserData() {
        with(sharedPreferences.edit()) {
            putString("areaOfStudy", binding.areaOfStudy.text.toString())
            putString("homeUniversity", binding.homeUniversity.text.toString())
            putString("targetUniversity", binding.targetUniversity.text.toString())
            putString("languages", binding.languages.text.toString())
            apply()
        }
    }

    private fun retrieveCachedDataAndUpload() {
        val areaOfStudy = sharedPreferences.getString("areaOfStudy", null)
        val homeUniversity = sharedPreferences.getString("homeUniversity", null)
        val targetUniversity = sharedPreferences.getString("targetUniversity", null)
        val languages = sharedPreferences.getString("languages", null)

        if (listOf(areaOfStudy, homeUniversity, targetUniversity, languages).all { !it.isNullOrEmpty() }) {
            binding.areaOfStudy.setText(areaOfStudy)
            binding.homeUniversity.setText(homeUniversity)
            binding.targetUniversity.setText(targetUniversity)
            binding.languages.setText(languages)

            updateUserInfo()
            clearUserDataCache()
        }
    }

    private fun clearUserDataCache() {
        sharedPreferences.edit().clear().apply()
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
                imageUri = it
            }
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
        showProgressBar()
        val ref = storageReference.child("images/${firebaseAuth.currentUser?.uid}")
        if (imageUri != null){
            ref.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        imageUrl = uri.toString()
                        Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                        imageUrl?.let {
                            userUpdates["profileImageUrl"] = it
                        }
                        databaseReference.child(firebaseAuth.currentUser?.uid!!).updateChildren(userUpdates)
                            .addOnCompleteListener { task ->
                                hideProgressBar()
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "User Info Updated Successfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Failed to Update User Info", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                .addOnFailureListener { e ->
                    hideProgressBar()
                    Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            databaseReference.child(firebaseAuth.currentUser?.uid!!).updateChildren(userUpdates)
                .addOnCompleteListener { task ->
                    hideProgressBar()
                    if (task.isSuccessful) {
                        Toast.makeText(this, "User Info Updated Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to Update User Info", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun showProgressBar(){
        dialog = Dialog(this@EditActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }
}