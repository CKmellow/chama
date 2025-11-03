package com.example.chamapp.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages storing and retrieving the user's authentication token.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "chamapp_prefs"
        private const val AUTH_TOKEN = "auth_token"
    }

    /**
     * Saves the authentication token to SharedPreferences.
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(AUTH_TOKEN, token)
        editor.apply()
    }

    /**
     * Retrieves the authentication token from SharedPreferences.
     * Returns null if no token is found.
     */
    fun getAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    /**
     * Clears the authentication token (used for logout).
     */
    fun clearAuthToken() {
        val editor = prefs.edit()
        editor.remove(AUTH_TOKEN)
        editor.apply()
    }
}
