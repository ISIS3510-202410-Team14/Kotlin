package com.optic.moveon.view

import android.app.Activity
import android.content.*
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.optic.moveon.databinding.ActivityUpload22Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.*

class UploadActivity22 : AppCompatActivity() {

    private lateinit var binding: ActivityUpload22Binding
    private lateinit var storageReference: StorageReference
    private lateinit var btnSelectFile: Button
    private lateinit var btnUploadFile: Button
    private lateinit var tvFileName: TextView
    private lateinit var ivFilePreview: ImageView
    private lateinit var fileUri: Uri
    private lateinit var selectFileLauncher: ActivityResultLauncher<Intent>
    private val cachedMessages: MutableList<String> = mutableListOf()
    private var isUploading = false

    private lateinit var connectivityReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpload22Binding.inflate(layoutInflater)
        setContentView(binding.root)

        btnSelectFile = binding.btnSelectFile
        btnUploadFile = binding.btnUploadFile
        tvFileName = binding.tvFileName
        ivFilePreview = binding.ivFilePreview

        storageReference = FirebaseStorage.getInstance().reference

        selectFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null && result.data?.data != null) {
                fileUri = result.data?.data!!
                tvFileName.text = fileUri.path
                showFilePreview(fileUri)
            }
        }

        btnSelectFile.setOnClickListener {
            selectFile()
        }
        btnUploadFile.setOnClickListener {
            uploadFile()
        }

        // Register the connectivity receiver
        connectivityReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (isInternetAvailable()) {
                    uploadCachedMessages()
                }
            }
        }
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

    private fun selectFile() {
        val intent = Intent().apply {
            type = "*/*"
            action = Intent.ACTION_GET_CONTENT
        }
        selectFileLauncher.launch(Intent.createChooser(intent, "Selecciona un archivo"))
    }

    private fun showFilePreview(uri: Uri) {
        val contentResolver = contentResolver
        val mimeType = contentResolver.getType(uri)
        if (mimeType?.startsWith("image/") == true) {
            ivFilePreview.setImageURI(uri)
            ivFilePreview.visibility = ImageView.VISIBLE
        } else {
            ivFilePreview.visibility = ImageView.GONE
        }
    }

    private fun uploadFile() {
        if (isInternetAvailable()) {
            if (cachedMessages.isNotEmpty() && !isUploading) {
                isUploading = true
                uploadCachedMessages()
            } else {
                fileUri?.let { uri ->
                    uploadFileToFirebase(uri)
                } ?: run {
                    Toast.makeText(this, "Por favor selecciona el archivo que deseas subir", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            saveFileLocally()
            saveMessageLocally("Mensaje que deseas guardar")
            Toast.makeText(this, "No hay conexión a Internet. El mensaje se ha guardado localmente.", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveMessageLocally(message: String) {
        cachedMessages.add(message)
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun uploadCachedMessages() {
        if (isInternetAvailable()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val sharedPreferences = getSharedPreferences("CachedFiles", Context.MODE_PRIVATE)
                val cachedFiles = sharedPreferences.getStringSet("files", mutableSetOf()) ?: mutableSetOf()

                if (cachedFiles.isNotEmpty()) {
                    val iterator = cachedFiles.iterator()
                    while (iterator.hasNext()) {
                        val cachedFileUriString = iterator.next()
                        val cachedFileUri = Uri.parse(cachedFileUriString)
                        uploadFileToFirebase(cachedFileUri)
                        iterator.remove() // Remove the file after attempting upload
                    }

                    sharedPreferences.edit().putStringSet("files", cachedFiles).apply()
                }
            }
        } else {
            Toast.makeText(this, "No hay conexión a Internet. No se pueden enviar los archivos en caché en este momento.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveFileLocally() {
        val sharedPreferences = getSharedPreferences("CachedFiles", Context.MODE_PRIVATE)
        val cachedFiles = sharedPreferences.getStringSet("files", mutableSetOf()) ?: mutableSetOf()

        cachedFiles.add(fileUri.toString())

        sharedPreferences.edit().putStringSet("files", cachedFiles).apply()
    }

    private fun uploadFileToFirebase(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val contentResolver = contentResolver
                val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
                fileDescriptor?.use {
                    val fileReference = storageReference.child("uploads/${System.currentTimeMillis()}.${getFileExtension(uri)}")
                    fileReference.putFile(uri).await()
                    val downloadUrl = fileReference.downloadUrl.await()
                    saveFileUrlToDatabase(downloadUrl.toString())
                    isUploading = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@UploadActivity22, "Archivo subido correctamente", Toast.LENGTH_LONG).show()
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@UploadActivity22, "No se pudo abrir el archivo", Toast.LENGTH_LONG).show()
                        isUploading = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UploadActivity22, "Error al acceder al archivo: ${e.message}", Toast.LENGTH_LONG).show()
                    isUploading = false
                }
            }
        }
    }

    private fun saveFileUrlToDatabase(downloadUrl: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("uploads")
        val newUploadKey = databaseReference.push().key // Generate a new unique key
        newUploadKey?.let {
            databaseReference.child(it).setValue(downloadUrl)
                .addOnSuccessListener {
                    if (cachedMessages.isNotEmpty()) {
                        uploadCachedMessages()
                    } else {
                        isUploading = false
                        Toast.makeText(this, "URL guardada en la base de datos", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                    isUploading = false
                }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mime = android.webkit.MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }
}
