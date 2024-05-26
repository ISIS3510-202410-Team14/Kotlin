package com.optic.moveon.view

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.optic.moveon.databinding.ActivityChat2Binding
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityChat2Binding
    private lateinit var dbref: DatabaseReference
    private lateinit var chatList: ArrayList<Chat>
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChat2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        userRecyclerview = binding.detalladochatScrollView
        userRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        userRecyclerview.setHasFixedSize(true)
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val uid = UserSessionManager.getUid()
        val universityId = intent.getStringExtra("name")
        Log.i("ChatActivity2", universityId ?: "University ID is null")

        chatList = arrayListOf()
        universityId?.let {
            getUserData(it)
            binding.textViewUniversityName.text = it
            checkAndSendCachedMessages(it)
        }

        val adapter = AdapterChat(this, chatList)
        userRecyclerview.adapter = adapter

        binding.buttonSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                val chat = Chat(
                    id = uid,
                    mensaje = message,
                    hora = System.currentTimeMillis(),
                    name = uid
                )

                lifecycleScope.launch {
                    if (isInternetAvailable()) {
                        universityId?.let { saveMessageToFirebase(chat, it) }
                    } else {
                        saveMessageLocally(chat)
                        Toast.makeText(this@ChatActivity2, "No hay conexión a internet. El mensaje se guardará en caché", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.editTextMessage.text.clear()
            } else {
                Toast.makeText(this, "Por favor, escriba un mensaje", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveMessageLocally(chat: Chat) {
        val sharedPreferences = getSharedPreferences("CachedMessages", Context.MODE_PRIVATE)
        val cachedMessages = sharedPreferences.getStringSet("messages", mutableSetOf()) ?: mutableSetOf()

        if (cachedMessages.size < 5) {
            cachedMessages.add(chat.mensaje)
            sharedPreferences.edit().apply {
                putStringSet("messages", cachedMessages)
                apply()
            }
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
                        hora = System.currentTimeMillis(),
                        name = UserSessionManager.getUid()
                    )
                    lifecycleScope.launch {
                        saveMessageToFirebase(chat, universityId)
                    }
                    count++
                }

                if (cachedMessages.size > 5) {
                    Toast.makeText(this, "Hay mensajes adicionales en caché que no se pudieron enviar debido a la falta de conexión a internet", Toast.LENGTH_SHORT).show()
                }

                sharedPreferences.edit().remove("messages").apply()
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun getUserData(universityId: String) {
        dbref = FirebaseDatabase.getInstance().getReference("Chats/$universityId")

        dbref.orderByChild("hora").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    chatList.clear()
                    for (userSnapshot in snapshot.children.reversed()) {
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

    private suspend fun saveMessageToFirebase(chat: Chat, universityId: String) {
        val dbref = FirebaseDatabase.getInstance().getReference("Chats/$universityId").push()
        chat.id = dbref.key
        withContext(Dispatchers.IO) {
            try {
                dbref.setValue(chat).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChatActivity2, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChatActivity2, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
