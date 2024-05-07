package com.optic.moveon.view

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.optic.moveon.databinding.ActivityChat2Binding
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.Chat

class ChatActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityChat2Binding
    private lateinit var dbref: DatabaseReference
    private lateinit var chatList: ArrayList<Chat>

    private lateinit var userRecyclerview: RecyclerView






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChat2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        userRecyclerview = binding.detalladochatScrollView
        userRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        userRecyclerview.setHasFixedSize(true)
        val uid = UserSessionManager.getUid()


        val universityId = intent.getStringExtra("name")
        println(universityId)
        Log.i("hola", universityId ?: "University ID is null")

        chatList = arrayListOf<Chat>()
        if (universityId != null) {
            getUserData(universityId)
        }
        binding.textViewUniversityName.text = universityId



        val adapter = AdapterChat(this, chatList)
        userRecyclerview.adapter = adapter

        // Configuración del listener del botón de enviar mensaje
        binding.buttonSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString().trim()

            if (message.isNotEmpty()) {
                val chat = Chat(
                    id = uid,
                    mensaje = message,
                    hora =  System.currentTimeMillis(), // Reemplaza este valor con la hora actual si es necesario
                    name = uid
                )

                // Verificar conexión a internet antes de enviar el mensaje
                if (isInternetAvailable()) {
                    if (universityId != null) {
                        saveMessageToFirebase(chat,universityId)
                    }
                } else {
                    saveMessageLocally(chat)
                    Toast.makeText(this, "No hay conexión a internet. El mensaje se guardará en caché", Toast.LENGTH_SHORT).show()
                }

                // Limpiar el campo de texto después de enviar el mensaje
                binding.editTextMessage.text.clear()
            } else {
                // Notificar al usuario si el campo de mensaje está vacío
                Toast.makeText(this, "Por favor, escriba un mensaje", Toast.LENGTH_SHORT).show()
            }
        }

        // Verificar y enviar mensajes almacenados en caché cuando la conexión está disponible

        if (universityId != null) {
            checkAndSendCachedMessages(universityId)
        }
    }



    private fun saveMessageLocally(chat: Chat) {
        val sharedPreferences = getSharedPreferences("CachedMessages", Context.MODE_PRIVATE)
        val cachedMessages = sharedPreferences.getStringSet("messages", mutableSetOf()) ?: mutableSetOf()

        // Limitar el número de mensajes en caché a 5
        if (cachedMessages.size < 5) {
            cachedMessages.add(chat.mensaje)
            sharedPreferences.edit().putStringSet("messages", cachedMessages).apply()
        }
    }

    private fun checkAndSendCachedMessages(universityId: String) {
        if (isInternetAvailable()) {
            val sharedPreferences = getSharedPreferences("CachedMessages", Context.MODE_PRIVATE)
            val cachedMessages = sharedPreferences.getStringSet("messages", mutableSetOf()) ?: mutableSetOf()

            if (cachedMessages.isNotEmpty()) {
                val iterator = cachedMessages.iterator()
                var count = 0

                while (iterator.hasNext() && count < 5) {
                    val cachedMessage = iterator.next()
                    val chat = Chat(
                        id = UserSessionManager.getUid(),
                        mensaje = cachedMessage,
                        hora = System.currentTimeMillis(), // Obtener la hora actual en milisegundos
                        name = UserSessionManager.getUid()
                    )
                    saveMessageToFirebase(chat,universityId)
                    count++
                }

                // Si hay más de 5 mensajes sin enviar, mostrar aviso
                if (cachedMessages.size > 5) {
                    Toast.makeText(this, "Hay mensajes adicionales en caché que no se pudieron enviar debido a la falta de conexión a internet", Toast.LENGTH_SHORT).show()
                }

                // Limpiar mensajes en caché después de enviarlos
                sharedPreferences.edit().remove("messages").apply()
            }
        }
    }


    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun getUserData(universityId:String) {
        dbref = FirebaseDatabase.getInstance().getReference("Chats/$universityId")


        dbref.orderByChild("hora").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    chatList.clear()
                    for (userSnapshot in snapshot.children.reversed()) { // Revertir el orden de los mensajes
                        val university = userSnapshot.getValue(Chat::class.java)
                        university?.let { chatList.add(it) }
                    }
                    userRecyclerview.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error aquí si es necesario
            }
        })
    }


    private fun saveMessageToFirebase(chat: Chat,universityId: String) {
        val dbref = FirebaseDatabase.getInstance().getReference("Chats/$universityId").push()
        chat.id = dbref.key // Asignar la clave generada como ID del chat
        dbref.setValue(chat)
            .addOnSuccessListener {
                Toast.makeText(this, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show()
            }
    }
}