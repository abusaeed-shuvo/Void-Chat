package com.example.voidchat.data

interface Message {
	val msgId: String
	val senderId: String
	val receiverId: String

	fun isRelevant(senderId: String, receiverId: String): Boolean {
		return (this.senderId == senderId && this.receiverId == receiverId) || (this.senderId == receiverId && this.receiverId == senderId)
	}

}

data class TextMessage(
	val text: String? = null,
	override var msgId: String,
	override val senderId: String,
	override val receiverId: String,
) : Message {
	constructor() : this("", "", "", "")
}

data class MessagesWithImage(
	val imageLink: String = "",
	override val msgId: String,
	override val senderId: String,
	override val receiverId: String,

	) : Message{
	constructor() : this("", "", "", "")
	}