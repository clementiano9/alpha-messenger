package ng.inits.alphamessenger.ui.search_contact

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_search_contact.*
import kotlinx.android.synthetic.main.item_search_contact.*
import ng.inits.alphamessenger.R
import ng.inits.alphamessenger.data.Contact


class SearchContactActivity : AppCompatActivity() {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    var query: Query
    var contactAdapter: SearchAdapter

    init {
        query = database.child("users")
        contactAdapter = SearchAdapter(query)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreated")
        setContentView(R.layout.activity_search_contact)
        Log.d(TAG, "after setContentView")

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()

        search_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                contactAdapter.filter.filter(s.toString())
            }
        })
    }

    fun setupRecyclerView() {
        Log.d(TAG, "Setting up recycler view")

        /*val database = FirebaseDatabase.getInstance().reference
        val query = database.child("users")
        val contactAdapter = SearchAdapter(query)*/

        recycler_view.apply {
            adapter = contactAdapter
            layoutManager = LinearLayoutManager(this@SearchContactActivity)
            itemAnimator = DefaultItemAnimator()
        }

        contactAdapter.filter.filter("")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    class ContactViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(contact: Contact) {
            name.text = contact.name
            email.text = contact.email

            containerView?.setOnClickListener {
                Log.d(TAG, "Display contact name")
            }
        }
    }

    companion object {
        val TAG: String = SearchContactActivity::class.java.simpleName
    }
}
