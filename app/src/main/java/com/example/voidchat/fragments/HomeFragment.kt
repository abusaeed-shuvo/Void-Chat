package com.example.voidchat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.voidchat.R
import com.example.voidchat.adapters.UserAdapter
import com.example.voidchat.data.DBNODES
import com.example.voidchat.data.User
import com.example.voidchat.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment(), UserAdapter.ItemClick {
	private lateinit var binding: FragmentHomeBinding
	private lateinit var userDB: DatabaseReference
	private lateinit var adapter: UserAdapter
	private lateinit var firebaseUser: FirebaseUser

	var userList: MutableList<User> = mutableListOf()
	private val auth = FirebaseAuth.getInstance()
	private var currentUser: User? = null
	private val bundle = Bundle()


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentHomeBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		userDB = FirebaseDatabase.getInstance().reference
		FirebaseAuth.getInstance().currentUser?.let {
			firebaseUser = it
		}

		binding.logoutBtn.setOnClickListener {
			auth.signOut().apply {
				findNavController().navigate(R.id.action_homeFragment_to_signInFragment)
			}
		}

		binding.profileBtn.setOnClickListener {

			currentUser?.let {
				bundle.putString("id", it.userId)
				findNavController().navigate(R.id.action_homeFragment_to_profileFragment, bundle)
			}
		}

		adapter = UserAdapter(this@HomeFragment)
		binding.recyclerView.adapter = adapter
		getAvailableUser()


	}


	private fun getAvailableUser() {

		userDB.child(DBNODES.USER).addValueEventListener(object : ValueEventListener {
			override fun onDataChange(snapshot: DataSnapshot) {
				userList.clear()
				snapshot.children.forEach {
					val user: User = it.getValue(User::class.java)!!
					if (firebaseUser.uid != user.userId) {
						userList.add(user)
					} else {
						currentUser = user
						if (user.profilePicture != "no Link" && user.profilePicture != "") {
							binding.profileBtn.load(user.profilePicture)
						}
					}
				}
				adapter.submitList(userList)
			}

			override fun onCancelled(error: DatabaseError) {
				Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
			}
		})
	}

	override fun onItemClick(user: User) {
		bundle.putString("id", user.userId)
		findNavController().navigate(R.id.action_homeFragment_to_profileFragment, bundle)
	}


}