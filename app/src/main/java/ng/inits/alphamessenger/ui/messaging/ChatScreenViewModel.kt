package ng.inits.alphamessenger.ui.messaging

import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ng.inits.alphamessenger.data.Message
import ng.inits.alphamessenger.data.User
import org.joda.time.DateTime

/**
 * Created by Clement Ozemoya on 14/05/2018.
 */
class ChatScreenViewModel(var initChatId: String?, val recipientId: String?, val recipientName: String?,
                          val callback: () -> Unit = {}) : BaseObservable() {
    val chatId = ObservableField<String>(initChatId)
    var progressVisibility = ObservableInt(View.GONE)
    var message = ObservableField<String>()

    val database = FirebaseDatabase.getInstance().reference
    val user = FirebaseAuth.getInstance().currentUser
    lateinit var userObject: User

    init {
        database.child("users").child(user?.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userObject = snapshot.getValue(User::class.java)
            }

        })
    }

    fun sendClicked(view: View) {
        Log.d(TAG, "Send clicked: ${message.get()}")
        if (message.get().isNullOrEmpty()) return

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
        if (chatId.get().isNullOrEmpty()) {
            chatId.set(database.push().key)
            shouldUpdateContact = true
            Log.d(TAG, "New chat key created = ${chatId.get()}")
        }

        database.child("messages")
                .child(chatId.get())
                .child(key)
                .setValue(messageData)

        val updates = HashMap<String, Any>()
        updates["/chats/${user.uid}/${chatId.get()}/lastMessage"] = message.get()
        updates["/chats/${user.uid}/${chatId.get()}/lastMessageAt"] = timestamp
        updates["/chats/${user.uid}/${chatId.get()}/lastMessageSender"] = user.uid
        updates["/chats/$recipientId/${chatId.get()}/lastMessage"] = message.get()
        updates["/chats/$recipientId/${chatId.get()}/lastMessageAt"] = timestamp
        updates["/chats/$recipientId/${chatId.get()}/lastMessageSender"] = user.uid

        // Update the contact to reflect the chatId if the chat was newly created
        if (shouldUpdateContact) {
            updates["/contacts/${user.uid}/$recipientId/chatId"] = chatId.get() as Any
            updates["/contacts/$recipientId/${user.uid}/chatId"] = chatId.get() as Any

            // Add info to the chat data for newly created chats
            updates["/chats/${user.uid}/${chatId.get()}/id"] = chatId.get()
            updates["/chats/${user.uid}/${chatId.get()}/contactId"] = recipientId
            updates["/chats/${user.uid}/${chatId.get()}/contactName"] = recipientName!!
            updates["/chats/$recipientId/${chatId.get()}/id"] = chatId.get()
            updates["/chats/$recipientId/${chatId.get()}/contactId"] = user.uid
            updates["/chats/$recipientId/${chatId.get()}/contactName"] = userObject.name

        }

        database.updateChildren(updates) { databaseError, databaseReference ->
            if (databaseError != null) {
                Log.w(TAG, "Error updating database with message", databaseError.toException())
            } else {
                callback()
            }
        }

        message.set("") // Clear message field
    }

    companion object {
        val TAG: String = ChatScreenViewModel::class.java.simpleName
    }
}