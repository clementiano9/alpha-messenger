package ng.inits.alphamessenger.ui.messaging

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat_screen.*
import kotlinx.android.synthetic.main.item_message.view.*
import ng.inits.alphamessenger.R
import ng.inits.alphamessenger.data.Message
import ng.inits.alphamessenger.databinding.ActivityChatScreenBinding

class ChatScreenActivity : AppCompatActivity() {

    private var recipientName: String? = null
    private var recipientId: String? = null
    private var chatId: String? = null
    private lateinit var viewModel: ChatScreenViewModel
    private lateinit var binding: ActivityChatScreenBinding
    private lateinit var database: DatabaseReference

    private lateinit var query: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_screen)

        Log.d(TAG, "[ChatScreenActivity] onCreate")
        getDataFromIntent()
        viewModel = ChatScreenViewModel(chatId, recipientId)
        binding.model = viewModel

        setSupportActionBar(toolbar)
        supportActionBar?.title = recipientName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        database = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser
        query = database.child("messages").child(chatId).orderByChild("timestamp")

        setupRecyclerView()
    }

    private fun getDataFromIntent() {
        chatId = intent.getStringExtra(ChatContract.CHAT_ID)
        recipientName = intent.getStringExtra(ChatContract.CHAT_RECIPIENT_NAME)
        recipientId = intent.getStringExtra(ChatContract.CHAT_RECIPIENT_ID)
        Log.d(TAG, "Recipient name: $recipientName")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up recycler view")
        val _adapter = object: FirebaseRecyclerAdapter<Message, MessageViewHolder> (
                Message::class.java,
                R.layout.item_message,
                MessageViewHolder::class.java,
                query
        ) {
            override fun populateViewHolder(viewHolder: MessageViewHolder?, model: Message?, position: Int) {
                Log.d(TAG, "Populate view holder $position, $itemCount total, message: ${model?.message}")
                viewModel.progressVisibility.set(View.GONE)
                viewHolder?.bind(model)
            }

            override fun onChildChanged(type: ChangeEventListener.EventType?, index: Int, oldIndex: Int) {
                super.onChildChanged(type, index, oldIndex)
                Log.d(TAG, "Message was added")
                recycler_view.scrollToPosition(itemCount - 1 )
            }
        }

        val linearLayoutManager = LinearLayoutManager(this@ChatScreenActivity)
        linearLayoutManager.reverseLayout
        recycler_view.apply {
            adapter = _adapter
            layoutManager = linearLayoutManager
            itemAnimator = DefaultItemAnimator()
        }

        _adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                Log.d(TAG, "Data changed")
            }
        })

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {

            }
        })

    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val messageText = itemView.findViewById<TextView>(R.id.message)
        val dateText = itemView.findViewById<TextView>(R.id.date)

        fun bind(message: Message?) {
            Log.d(TAG, "MessageViewHolder: bind ${message?.message}")
            messageText.text = message?.message
            //messageText.date = message?.timestamp
        }
    }

    companion object {
        val TAG: String = ChatScreenActivity::class.java.simpleName
    }
}
