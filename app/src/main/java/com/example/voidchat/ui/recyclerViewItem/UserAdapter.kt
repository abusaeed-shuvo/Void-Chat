package com.example.voidchat.ui.recyclerViewItem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.voidchat.data.User
import com.example.voidchat.databinding.UserItemBinding

class UserAdapter(var itemClick: ItemClick) : ListAdapter<User, UserViewHolder>(comparator) {
	interface ItemClick {
		fun onItemClick(user: User)
	}


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
		return UserViewHolder(
			UserItemBinding.inflate(
				LayoutInflater.from(parent.context), parent, false
			)
		)
	}

	override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
		getItem(position).let {
			holder.binding.apply {

				userName.text = it.fullName
				userEmail.text = it.email

				if (it.bio.isNotEmpty()) {
					userBio.visibility = View.VISIBLE
					userBio.text = it.bio
				} else {
					userBio.visibility = View.GONE
				}

			}

			holder.itemView.setOnClickListener { _ ->
				itemClick.onItemClick(it)
			}
		}
	}

	companion object {
		var comparator = object : DiffUtil.ItemCallback<User>() {
			override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
				return oldItem == newItem
			}

			override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
				return oldItem == newItem
			}

		}
	}
}

class UserViewHolder(var binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root)