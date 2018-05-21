package ng.inits.alphamessenger.ui.profile

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*
import ng.inits.alphamessenger.R
import ng.inits.alphamessenger.common.snackbar
import ng.inits.alphamessenger.data.User

class ProfileActivity : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance().reference
    val currentUser = FirebaseAuth.getInstance().currentUser
    val profile by lazy { intent.getParcelableExtra<User>("user") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
//        actionBar.setDisplayHomeAsUpEnabled(true)

        // get user from intent

        name.text = profile.name
        email.text = profile.email

        add_btn.setOnClickListener {
            Log.d(TAG, "Add to contacts")
            writeContactsToDb()

            snackbar("Added to contacts")
            add_btn.visibility = View.INVISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    fun writeContactsToDb() {
        database.child("users").child(currentUser?.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val myUser = snapshot.getValue(User::class.java)

                // Write to both current user's contacts and this user's contacts
                database.child("contacts").child(currentUser?.uid).child(profile.id).setValue(profile)
                database.child("contacts").child(profile.id).child(currentUser?.uid).setValue(myUser)
            }

        })
    }

    companion object {
        val TAG: String = ProfileActivity::class.java.simpleName
    }
}
