package com.example.chamapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.chamapp.api.UpdateProfileRequest
import com.example.chamapp.databinding.FragmentProfileBinding

class ProfileFragment : DialogFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchProfile()

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.etFirstName.setText(it.first_name)
                binding.etLastName.setText(it.last_name)
                binding.etPhoneNumber.setText(it.phone_number)
            }
        }

        viewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSave.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val request = UpdateProfileRequest(
                first_name = if (firstName.isNotEmpty()) firstName else null,
                last_name = if (lastName.isNotEmpty()) lastName else null,
                phone_number = if (phoneNumber.isNotEmpty()) phoneNumber else null,
                password = if (password.isNotEmpty()) password else null
            )

            viewModel.updateProfile(request)
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
