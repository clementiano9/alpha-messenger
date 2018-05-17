package ng.inits.alphamessenger.ui.search_contact

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.Query
import ng.inits.alphamessenger.R
import ng.inits.alphamessenger.data.Contact


/**
 * Created by Clement Ozemoya on 17/05/2018.
 */
class SearchAdapter(query: Query) : FirebaseRecyclerAdapter<Contact, SearchAdapter.ViewHolder>(
        Contact::class.java,
        R.layout.item_search_contact,
        ViewHolder::class.java,
        query
), Filterable {

    val user = FirebaseAuth.getInstance().currentUser

    val mContacts = arrayListOf<Contact>()
    val filteredContacts = arrayListOf<Contact>()

    override fun getItem(position: Int): Contact {
        return filteredContacts[position]
    }

    override fun getItemCount(): Int {
        return filteredContacts.size
    }

    override fun onChildChanged(type: ChangeEventListener.EventType?, index: Int, oldIndex: Int) {
        super.onChildChanged(type, index, oldIndex)
        Log.d(TAG, "onChildChanged")

        val filteredContacts = arrayListOf<Contact>()
        for (contact in getCopyOfDatabaseSnapshots()) {
            if (contact.id != user?.uid)
                filteredContacts.add(contact)
        }

        // Reset
        mContacts.clear()
        mContacts.addAll(filteredContacts)
        Log.d(TAG, "Children : ${mContacts.size}")
        notifyDataSetChanged()
    }

    /**
     * @return A copy of mSnapshots
     */
    private fun getCopyOfDatabaseSnapshots(): List<Contact> {
        val contacts = arrayListOf<Contact>()
        for (i in 0 until super.getItemCount()) {
            contacts.add(super.getItem(i))
        }
        return contacts
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                Log.d(TAG, "Perform filtering on $constraint")
                val filterResults = FilterResults()
                val results = arrayListOf<Contact>()
                if (!constraint.isNullOrEmpty()) {
                    for (contact in mContacts) {
                        Log.d(TAG, "Check ${contact.name} against ${constraint.toString()}")
                        if (contact.name.startsWith(constraint.toString())
                                || contact.email.startsWith(constraint.toString())) {
                            results.add(contact)
                        }
                    }

                    filterResults.count = results.size
                    filterResults.values = results

                } else {
                    filterResults.count = 0
                    filterResults.values = arrayListOf<Contact>()
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                filteredContacts.clear()
                filteredContacts.addAll(results.values as ArrayList<Contact>)
                Log.d(TAG, "Publish result ${filteredContacts.size}")
                notifyDataSetChanged()
            }
        }
    }

    override fun populateViewHolder(viewHolder: ViewHolder?, model: Contact?, position: Int) {
        viewHolder?.bind(model!!)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)
        val email = itemView.findViewById<TextView>(R.id.email)

        fun bind(contact: Contact) {
            name.text = contact.name
            email.text = contact.email
        }
    }

    companion object {
        val TAG:String = SearchAdapter::class.java.simpleName
    }

}