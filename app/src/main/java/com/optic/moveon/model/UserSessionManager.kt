package com.optic.moveon.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

object UserSessionManager {

    private const val PREF_NAME = "UserSessionPref";
    private const val KEY_IS_LOGGED_IN = "isLoggedIn";
    private const val KEY_UID = "uid";  // Constante para el UID del usuario
    private lateinit var sharedPreferences: SharedPreferences;

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    fun saveSession(uid: String?) {  // Agregar parámetro UID
        val editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        if (uid != null) {
            editor.putString(KEY_UID, uid);  // Guardar el UID en las preferencias
        }
        editor.apply();
    }

    fun getUid(): String? {
        return sharedPreferences.getString(KEY_UID, null);  // Recuperar el UID guardado
    }


    fun signOut() {
        val editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        FirebaseAuth.getInstance().signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}
