package ng.inits.alphamessenger.ui.chat


import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chats.*

import ng.inits.alphamessenger.R
import ng.inits.alphamessenger.common.inflate
import ng.inits.alphamessenger.data.Chat
import ng.inits.alphamessenger.databinding.FragmentChatsBinding
import ng.inits.alphamessenger.ui.messaging.ChatContract
import ng.inits.alphamessenger.ui.messaging.ChatScreenActivity

/**
 * List of chat threads
 *
 */
class ChatsFragment : Fragment() {

    lateinit var binding: FragmentChatsBinding
    var viewModel = ChatsViewModel()
    lateinit var _adapter: ChatsAdapter
    lateinit var database: DatabaseReference
    lateinit var query: Query

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false)
        binding.model = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchChats()
        database = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser
        query = database.child("chats").child(user?.uid)

        val options = FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat::class.java)
                .build()

        _adapter = ChatsAdapter(options, activity)

        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = _adapter
        }

        // Observe changes to the adapter
        _adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                Log.d(TAG, "Data changed")
            }
        })

        // Initial setup to check if there are items in this list
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                isChatEmpty()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                isChatEmpty()
            }

        })
    }

    private fun isChatEmpty() {
        Log.d(TAG, "Check if there are no chats")
        if (_adapter.itemCount < 1) {
            viewModel.setEmpty(true)
        } else {
            viewModel.setEmpty(false)
        }
    }

    inner class ChatsAdapter(val options: FirebaseRecyclerOptions<Chat>, val context: Context?)
        : FirebaseRecyclerAdapter<Chat , ChatViewHolder> (options) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
            val view = parent.inflate(R.layout.item_chat)
            return ChatViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ChatViewHolder, position: Int, chat: Chat) {

            viewModel.progressVisibility.set(View.GONE)

            viewHolder?.itemView?.setOnClickListener {
                Log.d(TAG, "Chat clicked: ${chat.contactName}")
                val intent = Intent(context, ChatScreenActivity::class.java)
                intent.apply {
                    putExtra(ChatContract.CHAT_ID, chat.id)
                    putExtra(ChatContract.CHAT_RECIPIENT_ID, chat.contactId)
                    putExtra(ChatContract.CHAT_RECIPIENT_NAME, chat.contactName)
                }
                startActivity(intent)
            }

            viewHolder.bind(chat)
        }

    }

    override fun toString(): String = "Messages"

    class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val message: TextView = itemView.findViewById(R.id.message)

        fun bind(chat: Chat?) {
            name.text = chat?.contactName
            message.text = chat?.lastMessage
        }
    }

    companion object {
        val TAG: String = ChatsFragment::class.java.simpleName

        fun newInstance() = ChatsFragment()
    }
}
