package com.optic.moveon.model

import android.content.Context
import android.content.SharedPreferences

object UserSessionManager {

    private const val PREF_NAME = "UserSessionPref"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveSession() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        // Add other session data to be saved
        editor.apply()
    }

    fun signOut() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // You can add other methods to get/set session data as needed
}