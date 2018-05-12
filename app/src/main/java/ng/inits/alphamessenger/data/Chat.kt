package ng.inits.alphamessenger.data

/**
 * Created by Clement Ozemoya on 12/05/2018.
 */
data class Chat(
        var id: String = "",
        var message: String = "",
        var senderId: String = "",
        var senderName: String = "",
        var receiverId: String = "",
        var receiverName: String = "",
        var timestamp: Long = 0L
)