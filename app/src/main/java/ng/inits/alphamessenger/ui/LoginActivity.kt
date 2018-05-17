package ng.inits.alphamessenger.ui

import android.Manifest
import android.app.LoaderManager
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import ng.inits.alphamessenger.MainActivity
import ng.inits.alphamessenger.R
import ng.inits.alphamessenger.data.PreferenceManager
import ng.inits.alphamessenger.data.User
import ng.inits.alphamessenger.ui.setup.SetupActivity
import java.util.*

class LoginActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var pref: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        pref = PreferenceManager(this)

        populateAutoComplete()
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        login_button.setOnClickListener { attemptLogin() }
        create_account_btn.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "User is logged in already")

            val intent = if (pref.hasPassedSetup()) Intent(this, MainActivity::class.java)
                else Intent(this, SetupActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // not signed in
            val intent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(AuthUI.IdpConfig.PhoneBuilder().build()))
                    .build()
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    private fun attemptLogin() {
        email.error = null
        password.error = null

        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!emailStr.matches(Patterns.EMAIL_ADDRESS.toRegex())) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (TextUtils.isEmpty(passwordStr) && passwordStr.length < 4) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            showProgress(true)
            login(emailStr, passwordStr)
        }
    }

    private fun login(emailStr: String, passwordStr: String) {
        Log.d(TAG, "Logging in")
        firebaseAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "Signed in")
                        Toast.makeText(this, "Signed in as ${it.result.user.email}", Toast.LENGTH_LONG).show()

                        // Check if user has setup his profile
                        val database = FirebaseDatabase.getInstance().reference
                        database.child("users").child(it.result.user.uid).addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onCancelled(error: DatabaseError?) {
                                Log.w(TAG, "Database error: ${error?.message}", error?.toException())
                            }

                            override fun onDataChange(snapshot: DataSnapshot?) {
                                val user = snapshot?.getValue(User::class.java)
                                Log.d(TAG, "User details: ${user?.email}")
                                if (TextUtils.isEmpty(user?.name)) {
                                    Log.w(TAG, "User has not setup profile")
                                    val intent = Intent(this@LoginActivity, SetupActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                } else {
                                    pref.markPassedSetup()
                                    Log.d(TAG, "Continue to MainActivity")
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }

                        })
                    } else {
                        // Unsuccessful
                        Log.w(TAG, "Sign in failure", it.exception)
                        Toast.makeText(this, "Incorrect email/password", Toast.LENGTH_SHORT).show()
                        showProgress(false)
                    }
                }

    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }

        loaderManager.initLoader(0, null, this)
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            Snackbar.make(email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok,
                            { requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS) })
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }

    private fun showProgress(show: Boolean) {
        email_login_form.visibility = if (show) View.INVISIBLE else View.VISIBLE
        login_progress.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        email.setAdapter(adapter)
    }

    object ProfileQuery {
        val PROJECTION = arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
        val ADDRESS = 0
        val IS_PRIMARY = 1
    }

    companion object {

        //Id to identity READ_CONTACTS permission request
        private val TAG: String = LoginActivity::class.java.simpleName
        private const val REQUEST_READ_CONTACTS = 0
        private val RC_SIGN_IN = 3
    }
}
