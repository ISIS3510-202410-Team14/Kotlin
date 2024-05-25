package com.optic.moveon.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.database.FirebaseDatabase
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityUploadBinding

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding

    private var fileUri: Uri? = null
    private lateinit var storageReference: StorageReference
    private lateinit var btnSelectFile: Button
    private lateinit var btnUploadFile: Button
    private lateinit var tvFileName: TextView
    private lateinit var ivFilePreview: ImageView

    private lateinit var selectFileLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnSelectFile = binding.btnSelectFile
        btnUploadFile = binding.btnUploadFile
        tvFileName = binding.tvFileName
        ivFilePreview = binding.ivFilePreview

        storageReference = FirebaseStorage.getInstance().reference

        selectFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null && result.data?.data != null) {
                fileUri = result.data?.data
                tvFileName.text = fileUri?.path
                showFilePreview(fileUri)
            }
        }

        btnSelectFile.setOnClickListener {
            selectFile()
        }

        btnUploadFile.setOnClickListener {
            fileUri?.let {
                uploadFile(it)
            } ?: run {
                Toast.makeText(this, "Por favor selecciona el archivo el cual desea subir", Toast.LENGTH_LONG).show()
            }
        }

        // Verificar y cargar archivos almacenados en caché
        checkAndUploadCachedFiles()
    }


    private fun selectFile() {
        val intent = Intent().apply {
            type = "*/*"
            action = Intent.ACTION_GET_CONTENT
        }
        selectFileLauncher.launch(Intent.createChooser(intent, "Selecciona un archivo"))
    }

    private fun showFilePreview(uri: Uri?) {
        uri?.let {
            val contentResolver = contentResolver
            val mimeType = contentResolver.getType(uri)
            if (mimeType?.startsWith("image/") == true) {
                ivFilePreview.setImageURI(uri)
                ivFilePreview.visibility = ImageView.VISIBLE
            } else {
                ivFilePreview.visibility = ImageView.GONE
            }
        }
    }

    private fun uploadFile(uri: Uri) {
        if (isInternetAvailable()) {
            val fileReference = storageReference.child("uploads/${System.currentTimeMillis()}.${getFileExtension(uri)}")
            fileReference.putFile(uri)
                .addOnSuccessListener {
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        saveFileUrlToDatabase(downloadUrl)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
        } else {
            saveFileLocally(uri)
            Toast.makeText(this, "No hay conexión a internet. El archivo se guardará en caché", Toast.LENGTH_SHORT).show()
        }
    }


    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    private fun saveFileLocally(uri: Uri) {
        val sharedPreferences = getSharedPreferences("CachedFiles", Context.MODE_PRIVATE)
        val cachedFiles = sharedPreferences.getStringSet("files", mutableSetOf()) ?: mutableSetOf()

        // Limitar el número de archivos en caché a 5
        if (cachedFiles.size < 5) {
            cachedFiles.add(uri.toString())
            sharedPreferences.edit().putStringSet("files", cachedFiles).apply()
        }
    }








    private fun saveFileUrlToDatabase(downloadUrl: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("uploads")
        databaseReference.child("val").setValue(downloadUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "URL guardada en la base de datos", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mime = android.webkit.MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }
}
