package com.optic.moveon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()


        binding.buttonRegister.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPass = binding.confirmpassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPass.isNotEmpty()){
                if (password == confirmPass){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful){
                            val intent = Intent(this, AuthActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this, "JUMMMMM algo pasa", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Empty Fields Are not Allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}