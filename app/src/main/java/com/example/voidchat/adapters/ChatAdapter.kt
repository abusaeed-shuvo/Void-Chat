package com.example.voidchat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.voidchat.R
import com.example.voidchat.data.TextMessage

class ChatAdapter(var userIdSelf: String) : ListAdapter<TextMessage, ChatAdapter.ChatViewHolder>(
	comparator
) {

	val left: Int = 1
	val right: Int = 2


	inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		var messageTV: TextView = itemView.findViewById(R.id.chatTV)

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
		if (viewType == right) {
			var view = LayoutInflater.from(parent.context)
				.inflate(R.layout.send_message_item, parent, false)
			return ChatViewHolder(view)
		} else {
			var view = LayoutInflater.from(parent.context)
				.inflate(R.layout.receive_message_item, parent, false)
			return ChatViewHolder(view)
		}
	}

	override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
		val message = getItem(position)
		holder.messageTV.text = message.text
	}


	override fun getItemViewType(position: Int): Int {
		return if (getItem(position).senderId == userIdSelf) right else left
	}


	companion object {
		val comparator = object : DiffUtil.ItemCallback<TextMessage>() {
			override fun areItemsTheSame(oldItem: TextMessage, newItem: TextMessage): Boolean {
				return oldItem == newItem
			}

			override fun areContentsTheSame(oldItem: TextMessage, newItem: TextMessage): Boolean {
				return oldItem == newItem
			}

		}
	}


}