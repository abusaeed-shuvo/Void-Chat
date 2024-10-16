package com.example.voidchat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.voidchat.R
import com.example.voidchat.databinding.FragmentChatBinding


class ChatFragment : Fragment() {


	private lateinit var binding: FragmentChatBinding


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentChatBinding.inflate(inflater, container, false)
		binding.btnBack.setOnClickListener { findNavController().navigate(R.id.action_chatFragment_to_homeFragment) }



		return binding.root
	}


}