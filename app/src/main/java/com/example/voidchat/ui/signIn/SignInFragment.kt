package com.example.voidchat.ui.signIn

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.voidchat.R
import com.example.voidchat.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    lateinit var firebaseUser: FirebaseUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        FirebaseAuth.getInstance().currentUser?.let {
            firebaseUser = it
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
        }

        binding.signUpTV.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }


        binding.btnSignIn.setOnClickListener {
            val email = binding.inputSignInEmail.text.toString().trim()
            val password = binding.inputSignInPassword.text.toString().trim()


//            if (isEmailValid(email) && isPasswordValid(password))
            if (isEmailValid(email)) {
                loginUser(email, password)


            } else {
                Toast.makeText(activity, "Invalid Email and Password", Toast.LENGTH_SHORT).show()
            }


        }

        return binding.root
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun loginUser(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser


                Toast.makeText(context, "${user?.email} Has Login Successfully", Toast.LENGTH_SHORT)
                    .show()

                findNavController().navigate(R.id.action_signInFragment_to_homeFragment)


            } else {
                Toast.makeText(activity, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}