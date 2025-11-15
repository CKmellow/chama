package com.example.chamapp.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages storing and retrieving the user's session data, including token and name.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "chamapp_prefs"
        private const val AUTH_TOKEN = "auth_token"
        private const val USER_FIRST_NAME = "user_first_name"
        private const val USER_LAST_NAME = "user_last_name"
        private const val USER_ID = "user_id"
    }

    fun saveUserId(userId: String?) {
        val editor = prefs.edit()
        editor.putString(USER_ID, userId)
        editor.apply()
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID, null)
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
     * Saves the user's first and last names to SharedPreferences.
     */
    fun saveUserName(firstName: String?, lastName: String?) {
        val editor = prefs.edit()
        editor.putString(USER_FIRST_NAME, firstName)
        editor.putString(USER_LAST_NAME, lastName)
        editor.apply()
    }

    /**
     * Saves the user's first name, last name, and email to SharedPreferences.
     */
    fun saveUserDetails(firstName: String?, lastName: String?, email: String?) {
        val editor = prefs.edit()
        editor.putString(USER_FIRST_NAME, firstName)
        editor.putString(USER_LAST_NAME, lastName)
        editor.putString("user_email", email)
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
     * Retrieves the user's first and last names from SharedPreferences.
     * Returns a Pair of (firstName, lastName). Values can be null if not found.
     */
    fun getUserName(): Pair<String?, String?> {
        val firstName = prefs.getString(USER_FIRST_NAME, null)
        val lastName = prefs.getString(USER_LAST_NAME, null)
        return Pair(firstName, lastName)
    }


    /**
     * Clears all session data (token and user names).
     */
    fun clearSession() {
        val editor = prefs.edit()
        editor.remove(AUTH_TOKEN)
        editor.remove(USER_FIRST_NAME)
        editor.remove(USER_LAST_NAME)
        editor.apply()
    }
}
