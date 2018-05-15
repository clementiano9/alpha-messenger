package ng.inits.alphamessenger.ui.messaging

import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ng.inits.alphamessenger.data.Message
import org.joda.time.DateTime

/**
 * Created by Clement Ozemoya on 14/05/2018.
 */
class ChatScreenViewModel(var chatId: String?, val recipientId: String?) : BaseObservable() {
    var progressVisibility = ObservableInt(View.GONE)
    var message = ObservableField<String>()

    val database = FirebaseDatabase.getInstance().reference
    val user = FirebaseAuth.getInstance().currentUser

    fun sendClicked(view: View) {
        Log.d(TAG, "Send clicked: ${message.get()}")
        if (message.get().isEmpty()) return

        // Create a new message at /messages/{chatId} and update lastMessage and lastMessageAt at /chats/{userId} for both users
        val key = database.push().key
        val timestamp = DateTime().millis
        val messageData = Message(
                id = key,
                message = message.get(),
                receiverId = recipientId!!,
                senderId = user?.uid!!,
                timestamp = timestamp
        )

        // If a chat has not been initiated/created btw the two people
        // Create chat data for each person
        var shouldUpdateContact = false
        if (chatId.isNullOrEmpty()) {
            chatId = database.push().key
            shouldUpdateContact = true
        }

        database.child("messages")
                .child(chatId)
                .child(key)
                .setValue(messageData)

        val updates = HashMap<String, Any>()
        updates["/chats/${user.uid}/$chatId/lastMessage"] = message.get()
        updates["/chats/${user.uid}/$chatId/lastMessageAt"] = timestamp
        updates["/chats/$recipientId/$chatId/lastMessage"] = message.get()
        updates["/chats/$recipientId/$chatId/lastMessage"] = timestamp

        // Update the contact to reflect the chatId if the chat was newly created
        if (shouldUpdateContact) {
            updates["/contacts/${user.uid}/$recipientId/chatId"] = chatId as Any
            updates["/contacts/$recipientId/${user.uid}/chatId"] = chatId as Any
        }

        database.updateChildren(updates) { databaseError, databaseReference ->
            if (databaseError != null) {
                Log.w(TAG, "Error updating database with message", databaseError.toException())
            }
        }

        message.set("") // Clear message field
    }

    companion object {
        val TAG: String = ChatScreenViewModel::class.java.simpleName
    }
}