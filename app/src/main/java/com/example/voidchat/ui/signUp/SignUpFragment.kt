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
import com.example.voidchat.data.DBNODES
import com.example.voidchat.data.User
import com.example.voidchat.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var userDB: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        userDB = FirebaseDatabase.getInstance().reference

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

                saveUserToDatabase(auth.currentUser?.uid, email, userName)
            } else {
                Toast.makeText(activity, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToDatabase(uid: String?, email: String, userName: String) {
        uid?.let {
            val user = User(userId = uid, email = email, fullName = userName)
            userDB.child(DBNODES.USER).child(it).setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                } else {
                    Toast.makeText(context, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

}