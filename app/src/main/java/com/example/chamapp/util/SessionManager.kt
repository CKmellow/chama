package com.example.chamapp.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages storing and retrieving the user's authentication token and user data.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "chamapp_prefs"
        private const val AUTH_TOKEN = "auth_token"
        private const val USER_FIRST_NAME = "user_first_name"
        private const val USER_ID = "user_id"
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

    /**
     * Saves the user's first name to SharedPreferences.
     */
    fun saveFirstName(firstName: String) {
        val editor = prefs.edit()
        editor.putString(USER_FIRST_NAME, firstName)
        editor.apply()
    }

    /**
     * Retrieves the user's first name from SharedPreferences.
     * Returns null if no first name is found.
     */
    fun getFirstName(): String? {
        return prefs.getString(USER_FIRST_NAME, null)
    }

    /**
     * Clears the user's first name.
     */
    fun clearFirstName() {
        val editor = prefs.edit()
        editor.remove(USER_FIRST_NAME)
        editor.apply()
    }

    /**
     * Saves the user's ID to SharedPreferences.
     */
    fun saveUserId(userId: String) {
        val editor = prefs.edit()
        editor.putString(USER_ID, userId)
        editor.apply()
    }

    /**
     * Retrieves the user's ID from SharedPreferences.
     * Returns null if no ID is found.
     */
    fun getUserId(): String? {
        return prefs.getString(USER_ID, null)
    }

    /**
     * Clears the user's ID.
     */
    fun clearUserId() {
        val editor = prefs.edit()
        editor.remove(USER_ID)
        editor.apply()
    }

    /**
     * Clears all session data.
     */
    fun clearSession() {
        clearAuthToken()
        clearFirstName()
        clearUserId()
    }
}
