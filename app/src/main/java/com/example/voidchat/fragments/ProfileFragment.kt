package com.example.voidchat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.voidchat.R
import com.example.voidchat.data.DBNODES
import com.example.voidchat.data.User
import com.example.voidchat.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ProfileFragment : Fragment() {
	private lateinit var binding: FragmentProfileBinding
	private lateinit var userDB: DatabaseReference
	private var userId: String = ""
	private var bundle: Bundle = Bundle()
	private lateinit var userStorage: StorageReference


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentProfileBinding.inflate(inflater, container, false)

		return binding.root
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		userDB = FirebaseDatabase.getInstance().reference
		userStorage = FirebaseStorage.getInstance().reference

		arguments?.getString("id", "")?.let {
			userId = it
			getUserById(it)
		}

		FirebaseAuth.getInstance().currentUser?.let {
			if (it.uid == userId) {
				binding.messageBtn.text = "Edit Profile"
			} else {
				binding.messageBtn.text = "Message"
			}
		}



		binding.goHomeBtn.setOnClickListener {
			findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
		}

		binding.messageBtn.setOnClickListener {
			bundle.putString("id", userId)

			if (binding.messageBtn.text == "Edit Profile") {
				findNavController().navigate(
					R.id.action_profileFragment_to_profileEditFragment,
					bundle
				)
			} else {

				findNavController().navigate(R.id.action_profileFragment_to_chatFragment, bundle)
			}

		}

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
							Glide.with(requireContext()).load(it.profilePicture)
								.placeholder(R.drawable.person_border_svgrepo_com)
								.into(userProfileIcon)
						}
					}
				}

				override fun onCancelled(error: DatabaseError) {
					Toast.makeText(context, "${error.message} ", Toast.LENGTH_SHORT).show()
				}
			}
		)
	}
}