package com.optic.moveon.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityMainBinding
import com.optic.moveon.databinding.ActivityChat2Binding
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.Chat
import com.optic.moveon.model.entities.University
import org.checkerframework.checker.units.qual.A

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
        userRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        userRecyclerview.setHasFixedSize(true)

        val uid = UserSessionManager.getUid()


        chatList = arrayListOf<Chat>()
        getUserData()
        val adapter = AdapterChat(this, chatList)
        userRecyclerview.adapter = adapter


        binding.buttonSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString().trim()

            if (message.isNotEmpty()) {
                val chat = Chat(
                    id = uid,
                    mensaje = message,
                    hora = 20,
                    name = uid

                )

                saveMessageToFirebase(chat)

                binding.editTextMessage.text.clear()
            } else {
                Toast.makeText(this, "Por favor, escriba un mensaje", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference("Chats/Harvard")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val university = userSnapshot.getValue(Chat::class.java)
                        chatList.add(university!!)
                    }
                    userRecyclerview.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun saveMessageToFirebase(chat: Chat) {
        val dbref = FirebaseDatabase.getInstance().getReference("Chats/Harvard")
            // Establecer el ID generado para el mensaje

            // Guardar el mensaje en la ubicación "Harvard" de la base de datos de Firebase
            dbref.child(chat.name.toString()).setValue(chat)
                .addOnSuccessListener {
                    // Éxito al guardar el mensaje
                    Toast.makeText(this, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    // Error al guardar el mensaje
                    Toast.makeText(this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show()
                }
        }








}