package com.example.voidchat.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.voidchat.R
import com.example.voidchat.data.DBNODES
import com.example.voidchat.data.User
import com.example.voidchat.databinding.FragmentProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileFragment : Fragment() {
	private lateinit var binding: FragmentProfileBinding
	private lateinit var userDB: DatabaseReference

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentProfileBinding.inflate(inflater, container, false)
		userDB = FirebaseDatabase.getInstance().reference
		arguments?.getString("id", "")?.let {
			getUserById(it)

		}
		binding.goHomeBtn.setOnClickListener {
			findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
		}
		binding.messageBtn.setOnClickListener {
			findNavController().navigate(R.id.action_profileFragment_to_chatFragment)
		}


		return binding.root
	}

	private fun getUserById(userId: String) {
		userDB.child(DBNODES.USER).child(userId).addValueEventListener(
			object : ValueEventListener {
				override fun onDataChange(snapshot: DataSnapshot) {
					snapshot.getValue(User::class.java)?.let {
						binding.apply {
							fullUserName.text = it.fullName
							userBio.text = it.bio
							emailTV.text = it.email
						}
					}
				}

				override fun onCancelled(error: DatabaseError) {
					Toast.makeText(context, "${error.message}", Toast.LENGTH_SHORT).show()
				}

			}
		)
	}


}