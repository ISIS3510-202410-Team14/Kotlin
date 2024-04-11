package com.optic.moveon.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityLoginBinding
import com.optic.moveon.model.UserSessionManager

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)




        //Se supone que aqui debería ir un contador que si le da click lo llevara a la vista de home

        binding.btnNext.setOnClickListener {
            logFirebaseEvent("ingreso_a_home")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        binding.textViewRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        //Se supone que aqui debería ir otro contador pero para la misma variable ya que este también lleva al user a la vista home
        binding.buttonLogin.setOnClickListener{
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful){
                        logFirebaseEvent("ingreso_a_home")
                        UserSessionManager.saveSession()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Empty Fields Are not Allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun logFirebaseEvent(eventName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, eventName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}