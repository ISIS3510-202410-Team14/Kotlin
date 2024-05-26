package com.optic.moveon.view

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityRegisterBinding
import com.optic.moveon.model.entities.User
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi


class RegisterActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dialog: Dialog
    private lateinit var networkReceiver: BroadcastReceiver

    override fun onStart() {
        super.onStart()
        // Registrar el BroadcastReceiver para escuchar cambios en la conectividad
        networkReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceive(context: Context?, intent: Intent?) {
                if (isNetworkAvailable(context!!)) {
                    val sharedPreferences = getSharedPreferences("UserCache", Context.MODE_PRIVATE)
                    val name = sharedPreferences.getString("name", "")
                    val email = sharedPreferences.getString("email", "")
                    val password = sharedPreferences.getString("password", "")
                    if (!name.isNullOrEmpty() && !email.isNullOrEmpty() && !password.isNullOrEmpty()) {
                        // Intentar registrar al usuario con los datos en caché
                        registerUser(name, email, password)
                        clearUserDataCache() // Limpia el caché una vez el registro es exitoso
                    }
                }
            }
        }
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        // Desregistrar el BroadcastReceiver cuando la actividad no está visible
        unregisterReceiver(networkReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")


        binding.buttonRegister.setOnClickListener {
            showProgressBar()

            val name = binding.name.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPass = binding.confirmpassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (password == confirmPass) {
                    if (isNetworkAvailable(this)) {
                        // Ejecutar la autenticación en un hilo separado
                        Thread {
                            Log.d("RegisterActivity", "Thread started")
                            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = firebaseAuth.currentUser?.uid
                                    if (uid != null) {
                                        val user = User(uid, name, email, password, null, null, null, null, null)
                                        databaseReference.child(uid).setValue(user).addOnCompleteListener { dbTask ->
                                            if (dbTask.isSuccessful) {
                                                runOnUiThread {
                                                    val intent = Intent(this, AuthActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            } else {
                                                runOnUiThread {
                                                    hideProgressBar()
                                                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    runOnUiThread {
                                        hideProgressBar()
                                        Toast.makeText(this, task.exception?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                Log.d("RegisterActivity", "Thread finished")
                                // Llamar a hideProgressBar() después de completar la autenticación
                                runOnUiThread { hideProgressBar() }
                            }
                        }.start()
                    } else {
                        // No hay conexión a internet, guardar datos en caché
                        cacheUserData(name, email, password)
                        hideProgressBar()
                        Toast.makeText(this, "No internet connection. Your data has been cached and will be processed once connectivity is restored.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                hideProgressBar()
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgressBar(){
        dialog = Dialog(this@RegisterActivity)
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

    fun cacheUserData(name: String, email: String, password: String) {
        val sharedPreferences = getSharedPreferences("UserCache", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("name", name)
            .putString("email", email)
            .putString("password", password)
            .apply()
    }

    fun clearUserDataCache() {
        val sharedPreferences = getSharedPreferences("UserCache", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    private fun registerUser(name: String, email: String, password: String) {
        showProgressBar()
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = firebaseAuth.currentUser?.uid
                if (uid != null) {
                    val user = User(uid, name, email, password)
                    databaseReference.child(uid).setValue(user).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            val intent = Intent(this, AuthActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, task.exception?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
            }
            hideProgressBar()
        }
    }

}