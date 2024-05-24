package com.optic.moveon.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage

import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityUploadBinding

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding

    private val PICK_FILE_REQUEST = 1
    private lateinit var fileUri: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var btnSelectFile: Button
    private lateinit var btnUploadFile: Button
    private lateinit var tvFileName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnSelectFile = binding.btnSelectFile
        btnUploadFile = binding.btnUploadFile
        tvFileName = binding.tvFileName

        storageReference = FirebaseStorage.getInstance().reference

        btnSelectFile.setOnClickListener {
            selectFile()
        }

        btnUploadFile.setOnClickListener {
            uploadFile()
        }





    }



    private fun selectFile() {
        val intent = Intent().apply {
            type = "*/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo"), PICK_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fileUri = data.data!!
            tvFileName.text = fileUri.path
        }
    }

    private fun uploadFile() {
        if (::fileUri.isInitialized) {
            val fileReference = storageReference.child("uploads/${System.currentTimeMillis()}.${getFileExtension(fileUri)}")
            fileReference.putFile(fileUri)
                .addOnSuccessListener {
                    Toast.makeText(this, "Archivo subido exitosamente", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Por favor selecciona un archivo primero", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mime = android.webkit.MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }
}

