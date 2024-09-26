package com.example.voidchat.ui.signIn

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.voidchat.R
import com.example.voidchat.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth


class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)

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

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        val passRegex = Regex("^(?=.*[A-Za-z])(?=.*[@$!%*#?&])[A-Za-z@$!%*#?&\\d]{6,}$")

        return password.matches(passRegex)
    }

    fun loginUser(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                //toast
                Toast.makeText(
                    activity,
                    "${user?.email} has logged in successfully",
                    Toast.LENGTH_SHORT
                ).show()

                // message
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Sign In")
                builder.setMessage("${user?.email} has logged in successfully")

                builder.setNeutralButton(
                    "Ok"
                ) { p0, _ -> p0.cancel() }

                val alertDialog = builder.create()
                alertDialog.show()
            } else {
                Toast.makeText(activity, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}