package ng.inits.alphamessenger.ui.search_contact

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import ng.inits.alphamessenger.R
import ng.inits.alphamessenger.data.Contact
import ng.inits.alphamessenger.data.User
import ng.inits.alphamessenger.ui.profile.ProfileActivity


/**
 * Created by Clement Ozemoya on 17/05/2018.
 */
class SearchAdapter(query: Query) : FirebaseRecyclerAdapter<User, SearchAdapter.ViewHolder>(
        User::class.java,
        R.layout.item_search_contact,
        ViewHolder::class.java,
        query
), Filterable {

    val user = FirebaseAuth.getInstance().currentUser

    val mPeople = arrayListOf<User>()    // Users
    val filteredPeople = arrayListOf<User>() // Filtered users
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val contactList = arrayListOf<Contact>()    // Added to contacts

    init {
        // Fetch users contacts to keep track of who has been added
        database.child("contacts").child(user?.uid).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {
                Log.w(TAG, "onCancelled", error?.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot?) {
                Log.d(TAG, "Users contacts fetched")
                contactList.clear()
                snapshot?.children?.forEach {
                    val user = it.getValue(Contact::class.java)
                    Log.d(TAG, "Contact: ${user.name}")
                    contactList.add(user)
                }
            }

        })
    }

    override fun getItem(position: Int): User {
        return filteredPeople[position]
    }

    override fun getItemCount(): Int {
        return filteredPeople.size
    }

    override fun onChildChanged(type: ChangeEventListener.EventType?, index: Int, oldIndex: Int) {
        super.onChildChanged(type, index, oldIndex)
        Log.d(TAG, "onChildChanged")

        val filteredContacts = arrayListOf<User>()
        // Remove current user's contact
        for (contact in getCopyOfDatabaseSnapshots()) {
            if (contact.id != user?.uid)
                filteredContacts.add(contact)
        }

        // Reset
        mPeople.clear()
        mPeople.addAll(filteredContacts)
        Log.d(TAG, "Children : ${mPeople.size}")
        notifyDataSetChanged()
    }

    /**
     * @return A copy of mSnapshots
     */
    private fun getCopyOfDatabaseSnapshots(): List<User> {
        val contacts = arrayListOf<User>()
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
                val results = arrayListOf<User>()
                if (!constraint.isNullOrEmpty()) {
                    for (person in mPeople) {
                        Log.d(TAG, "Check ${person.name} against ${constraint.toString()}")
                        if (person.name.startsWith(constraint.toString())
                                || person.email.startsWith(constraint.toString())) {
                            if (isUserContact(person))
                                Log.d(TAG, "Contacts already contains ${person.name}")
                            else
                                results.add(person)
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
                filteredPeople.clear()
                filteredPeople.addAll(results.values as ArrayList<User>)
                Log.d(TAG, "Publish result ${filteredPeople.size}")
                notifyDataSetChanged()
            }
        }
    }

    fun isUserContact(user: User) : Boolean {
        return contactList.any { it.id == user.id }
    }

    override fun populateViewHolder(viewHolder: ViewHolder?, model: User?, position: Int) {
        viewHolder?.bind(model!!)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)
        val email = itemView.findViewById<TextView>(R.id.email)

        fun bind(user: User) {
            name.text = user.name
            email.text = user.email
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProfileActivity::class.java)
                intent.putExtra("user", user)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        val TAG:String = SearchAdapter::class.java.simpleName
    }

}