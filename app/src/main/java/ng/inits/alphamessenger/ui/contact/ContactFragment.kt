package ng.inits.alphamessenger.ui.contact


import android.content.Context
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_contact.*
import ng.inits.alphamessenger.R
import ng.inits.alphamessenger.data.Contact
import ng.inits.alphamessenger.databinding.FragmentContactBinding

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

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setLoading()
        database = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser
        query = database.child("contacts").child(user?.uid)
        mAdapter = ContactsAdapter(query, activity)
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

    inner class ContactsAdapter(query: Query?, val context: Context) : FirebaseRecyclerAdapter<Contact, ContactsViewHolder> (
            Contact::class.java,
            R.layout.item_contact,
            ContactsViewHolder::class.java,
            query
    ) {

        override fun populateViewHolder(viewHolder: ContactsViewHolder?, model: Contact?, position: Int) {
            viewModel.progressVisibility.set(View.GONE)

            viewHolder?.itemView?.setOnClickListener {
                // Launch chat screen
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
