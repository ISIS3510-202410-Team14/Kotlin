package com.optic.moveon.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityLoginBinding
import com.optic.moveon.model.UserSessionManager

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var dialog: Dialog


    @RequiresApi(Build.VERSION_CODES.M)
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


        binding.buttonLogin.setOnClickListener {
            showProgressBar()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            // Verificar que se ingresaron datos en ambos campos
            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (isNetworkAvailable(this)) {
                    // Ejecutar la autenticación en un hilo separado
                    Thread {
                        Log.d("AuthThread", "Thread started")
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = firebaseAuth.currentUser?.uid
                                    UserSessionManager.saveSession(uid)
                                    logFirebaseEvent("ingreso_a_home")
                                    runOnUiThread {
                                        // Ir a la pantalla principal si la autenticación fue exitosa
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                } else {
                                    runOnUiThread {
                                        // Mostrar mensaje de error si la autenticación falló
                                        hideProgressBar()
                                        Toast.makeText(
                                            this,
                                            task.exception?.localizedMessage ?: "Login failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        Log.d("AuthThread", "Thread finished")
                    }.start()
                } else {
                    hideProgressBar()
                    Toast.makeText(
                        this,
                        "No Internet Connection. Please try again later.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                hideProgressBar()
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun logFirebaseEvent(eventName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, eventName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    private fun showProgressBar(){
        dialog = Dialog(this@AuthActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


}