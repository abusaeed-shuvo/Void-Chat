package com.example.voidchat.ui.signUp

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.voidchat.R
import com.example.voidchat.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.signInTV.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.inputSignUpEmail.text.toString().trim()
            val password = binding.inputSignUpPassword.text.toString().trim()
            val user = binding.inputSignUpName.text.toString().trim()

            if (isEmailValid(email) && isPasswordValid(password)) {
                signUpUser(user, email, password)
            } else {
                Toast.makeText(activity, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        val passRegex = Regex("^(?=.*[A-Za-z])(?=.*[@$!%*#?&])[A-Za-z@$!%*#?&\\d]{6,}$")

        return password.matches(passRegex)
    }

    private fun signUpUser(userName: String, email: String, password: String) {
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                // message
                Toast.makeText(activity, "New account created successfully!", Toast.LENGTH_SHORT)
                    .show()

                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            } else {
                Toast.makeText(activity, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}