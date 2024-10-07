package com.example.voidchat.ui.home

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
import com.example.voidchat.databinding.FragmentHomeBinding
import com.example.voidchat.ui.recyclerViewItem.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment(), UserAdapter.ItemClick {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var userDB: DatabaseReference
    private lateinit var adapter: UserAdapter
    var userList: MutableList<User> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        userDB = FirebaseDatabase.getInstance().reference
        val auth = FirebaseAuth.getInstance()

        binding.logoutBtn.setOnClickListener {

            auth.signOut().apply {
                findNavController().navigate(R.id.action_homeFragment_to_signInFragment)
            }

        }
        binding.profileBtn.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("id", auth.currentUser?.uid)
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment, bundle)
        }


        adapter = UserAdapter(this@HomeFragment)
        binding.recyclerView.adapter = adapter

        getAvailableUser()

        return binding.root
    }

    private fun getAvailableUser() {
        userDB.child(DBNODES.USER).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                snapshot.children.forEach {
                    val user: User = it.getValue(User::class.java)!!
                    userList.add(user)
                }
                adapter.submitList(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onItemClick(user: User) {
        var bundle = Bundle()
        bundle.putString("id", user.userId)
        findNavController().navigate(R.id.action_homeFragment_to_profileFragment, bundle)
    }


}