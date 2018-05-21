package ng.inits.alphamessenger.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Clement Ozemoya on 10/05/2018.
 */
@Parcelize
data class User(
        var id: String = "",
        var name: String = "",
        var email: String = ""
) : Parcelable

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
        var lastMessageAt: Long = 0L,
        var lastMessageSender: String = ""
)

data class Message(
        var id: String = "",
        var message: String = "",
        var senderId: String = "",
        var receiverId: String = "",
        var timestamp: Long = 0L
)