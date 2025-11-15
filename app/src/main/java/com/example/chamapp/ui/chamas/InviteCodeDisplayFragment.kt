package com.example.chamapp.ui.chamas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.chamapp.R
import com.example.chamapp.api.RetrofitClient
import kotlinx.coroutines.launch

class InviteCodeDisplayFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_invite_code_display, container, false)
        val code = arguments?.getString("inviteCode") ?: ""
        view.findViewById<TextView>(R.id.tvInviteCodeDisplay).text = code

        // Store invite code in Supabase via backend API
        if (code.isNotBlank()) {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.storeInviteCode(mapOf("code" to code))
                    if (response.isSuccessful) {
                        android.util.Log.d("InviteCodeDisplayFragment", "Invite code stored in Supabase.")
                    } else {
                        android.util.Log.e("InviteCodeDisplayFragment", "Failed to store invite code: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("InviteCodeDisplayFragment", "Error storing invite code: ${e.message}", e)
                }
            }
        }
        return view
    }
}
