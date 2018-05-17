package ng.inits.alphamessenger.ui.contact


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
import kotlinx.android.synthetic.main.fragment_contact.*
import ng.inits.alphamessenger.R
import ng.inits.alphamessenger.common.inflate
import ng.inits.alphamessenger.data.Chat
import ng.inits.alphamessenger.data.Contact
import ng.inits.alphamessenger.databinding.FragmentContactBinding
import ng.inits.alphamessenger.ui.messaging.ChatContract
import ng.inits.alphamessenger.ui.messaging.ChatScreenActivity

/**
 * A simple [Fragment] subclass.
 *
 */
class ContactFragment : Fragment() {

    lateinit var binding: FragmentContactBinding
    var viewModel = ContactsViewModel()
    lateinit var mAdapter: ContactsAdapter
    lateinit var database: DatabaseReference
    lateinit var query: Query

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact, container, false)
        binding.model = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setLoading()
        database = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser
        query = database.child("contacts").child(user?.uid)

        val options = FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(query, Contact::class.java)
                .build()

        mAdapter = ContactsAdapter(options, activity)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }

        // Observe changes to the adapter
        mAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                Log.d(TAG, "Data changed")
            }
        })

        // Initial setup to check if there are items in this list
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                isContactListEmpty()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                isContactListEmpty()
            }

        })
    }

    private fun isContactListEmpty() {
        Log.d(TAG, "Check if there are no contacts")
        if (mAdapter.itemCount < 1) {
            viewModel.setEmpty(true)
            Log.d(TAG, "No contacts")
        } else {
            viewModel.setEmpty(false)
            Log.d(TAG, "Contacts found ${mAdapter.itemCount}")
        }
    }

    inner class ContactsAdapter(options: FirebaseRecyclerOptions<Contact>, val context: Context?)
        : FirebaseRecyclerAdapter<Contact, ContactsViewHolder> (options) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
            return ContactsViewHolder(parent.inflate(R.layout.item_contact))
        }

        override fun onBindViewHolder(viewHolder: ContactsViewHolder, position: Int, model: Contact) {

            viewModel.progressVisibility.set(View.GONE)

            viewHolder?.itemView?.setOnClickListener {
                // Launch chat screen
                val intent = Intent(context, ChatScreenActivity::class.java)
                intent.apply {
                    putExtra(ChatContract.CHAT_ID, model?.chatId) // Undefined because a chat has not been initiated yet
                    putExtra(ChatContract.CHAT_RECIPIENT_ID, model?.id)
                    putExtra(ChatContract.CHAT_RECIPIENT_NAME, model?.name)
                }
                startActivity(intent)
            }

            viewHolder?.bind(model)
        }

    }

    class ContactsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val contactName: TextView = itemView.findViewById(R.id.contact_name)

        fun bind(contact: Contact?) {
            contactName.text = contact?.name
        }
    }

    override fun toString(): String {
        return "Contacts"
    }

    companion object {
        val TAG: String = ContactFragment::class.java.simpleName
        fun newInstance() : ContactFragment {
            return ContactFragment()
        }
    }

}
