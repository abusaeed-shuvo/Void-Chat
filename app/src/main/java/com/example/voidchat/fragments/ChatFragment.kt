package com.example.voidchat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.voidchat.R
import com.example.voidchat.adapters.ChatAdapter
import com.example.voidchat.data.DBNODES
import com.example.voidchat.data.TextMessage
import com.example.voidchat.data.User
import com.example.voidchat.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID


class ChatFragment : Fragment() {

	private lateinit var binding: FragmentChatBinding
	private lateinit var chatDB: DatabaseReference
	private lateinit var senderId: String
	private lateinit var receiverId: String
	private lateinit var adapter: ChatAdapter


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentChatBinding.inflate(inflater, container, false)


		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		chatDB = FirebaseDatabase.getInstance().reference

		FirebaseAuth.getInstance().currentUser?.let {
			senderId = it.uid
		}

		arguments?.let {
			receiverId = it.getString("id") ?: ""
		}

		binding.btnBack.setOnClickListener {
			findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
		}

		chatDB.child(DBNODES.USER).child(receiverId).addValueEventListener(object :
			ValueEventListener {
			override fun onDataChange(snapshot: DataSnapshot) {
				snapshot.getValue(User::class.java)?.let { user ->
					binding.apply {
						Glide.with(requireContext()).load(user.profilePicture)
							.placeholder(R.drawable.person_border_svgrepo_com).into(profileImage)
						profileName.text = user.fullName
						profileEmail.text = user.email
					}

				}
			}

			override fun onCancelled(error: DatabaseError) {
				Toast.makeText(
					requireContext(),
					"Database Error: ${error.message}",
					Toast.LENGTH_SHORT
				).show()
			}

		})

		binding.sendBtn.setOnClickListener {
			var textMessage = TextMessage(
				text = binding.messageET.text.toString(),
				msgId = "",
				senderId = senderId,
				receiverId = receiverId
			)
			sendMessage(textMessage)
		}

		adapter = ChatAdapter(senderId)

		binding.messageRCV.layoutManager = LinearLayoutManager(requireContext())
		binding.messageRCV.adapter = adapter

		messageToShow()
	}

	private fun messageToShow() {
		chatDB.child(DBNODES.CHAT).addValueEventListener(object : ValueEventListener {
			override fun onDataChange(snapshot: DataSnapshot) {
				var chatList = mutableListOf<TextMessage>()

				snapshot.children.forEach { snap ->
					snap.getValue(TextMessage::class.java)?.let { textMessage ->
						if (textMessage.isRelevant(senderId, receiverId)) {
							chatList.add(textMessage)
						}
					}
				}

				adapter.submitList(chatList.toList())
			}

			override fun onCancelled(error: DatabaseError) {
				Toast.makeText(
					requireContext(),
					"Database Error: ${error.message}",
					Toast.LENGTH_SHORT
				).show()
			}

		})
	}

	private fun sendMessage(textMessage: TextMessage) {
		var msgID = chatDB.push().key ?: UUID.randomUUID().toString()

		textMessage.msgId = msgID

		chatDB.child(DBNODES.CHAT).child(msgID).setValue(textMessage)
			.addOnCompleteListener { task ->
				if (task.isSuccessful) {
					Toast.makeText(
						requireContext(),
						"Message Sent Successfully",
						Toast.LENGTH_SHORT
					)
						.show()
					binding.messageET.setText("")
				} else {
					Toast.makeText(
						requireContext(),
						"Task Error: ${task.exception?.message}",
						Toast.LENGTH_SHORT
					).show()
				}

			}
	}


}