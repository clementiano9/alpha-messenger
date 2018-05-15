package ng.inits.alphamessenger.data

/**
 * Created by Clement Ozemoya on 10/05/2018.
 */
data class User(
        var id: String = "",
        var name: String = "",
        var email: String = ""
)

data class Contact(
        var id: String = "",
        var chatId: String = "",
        var name: String = "",
        var email: String = "",
        var addedAt: Long = 0L
)

data class Chat(
        var id: String = "",
        var lastMessage: String = "",
        var contactId: String = "",
        var contactName: String = "",
        var lastMessageAt: Long = 0L
)

data class Message(
        var id: String = "",
        var message: String = "",
        var senderId: String = "",
        var receiverId: String = "",
        var timestamp: Long = 0L
)