package com.optic.moveon.view

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.optic.moveon.databinding.ActivityUpload22Binding
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
                // Si hay mensajes en caché, envíalos
                isUploading = true
                uploadCachedMessages()
            } else {
                // Si no hay mensajes en caché o ya se está subiendo un archivo, sube el archivo actual
                fileUri?.let { uri ->
                    uploadFileToFirebase(uri)
                } ?: run {
                    Toast.makeText(this, "Por favor selecciona el archivo que deseas subir", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            // Si no hay conexión a Internet, guarda el mensaje en caché
            saveFileLocally() // Llama a la función saveFileLocally aquí
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
            val sharedPreferences = getSharedPreferences("CachedFiles", Context.MODE_PRIVATE)
            val cachedFiles = sharedPreferences.getStringSet("files", mutableSetOf()) ?: mutableSetOf()

            if (cachedFiles.isNotEmpty()) {
                val iterator = cachedFiles.iterator()
                var count = 0

                while (iterator.hasNext() && count < 5) {
                    val cachedFileUriString = iterator.next()
                    val cachedFileUri = Uri.parse(cachedFileUriString)
                    // Subir el archivo guardado en caché a Firebase
                    uploadFileToFirebase(cachedFileUri)
                    count++
                }

                // Si hay más de 5 archivos sin enviar, mostrar aviso
                if (cachedFiles.size > 5) {
                    Toast.makeText(this, "Hay archivos adicionales en caché que no se pudieron enviar debido a la falta de conexión a internet", Toast.LENGTH_SHORT).show()
                }

                // Limpiar archivos en caché después de enviarlos
                sharedPreferences.edit().remove("files").apply()
            }
        } else {
            // Si no hay conexión a Internet, mostrar un mensaje al usuario
            Toast.makeText(this, "No hay conexión a Internet. No se pueden enviar los archivos en caché en este momento.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveFileLocally() {
        val inputStream = contentResolver.openInputStream(fileUri)
        val outputStream: OutputStream = FileOutputStream(File(cacheDir, "cached_file"))
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
    }

    private fun uploadFileToFirebase(uri: Uri) {
        val fileReference = storageReference.child("uploads/${System.currentTimeMillis()}.${getFileExtension(uri)}")
        fileReference.putFile(uri)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    saveFileUrlToDatabase(downloadUrl)
                    // Después de subir el archivo, intenta subir los mensajes en caché
                    if (cachedMessages.isNotEmpty()) {
                        uploadCachedMessages()
                    } else {
                        isUploading = false
                        Toast.makeText(this, "Archivo subido correctamente", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                isUploading = false
            }
    }

    private fun saveFileUrlToDatabase(downloadUrl: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("uploads")
        databaseReference.child("val").setValue(downloadUrl)
            .addOnSuccessListener {
                // Si se ha subido correctamente el archivo, intenta subir los mensajes en caché
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

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mime = android.webkit.MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }
}
