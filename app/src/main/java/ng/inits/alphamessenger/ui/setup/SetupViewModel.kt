package ng.inits.alphamessenger.ui.setup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.*
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ng.inits.alphamessenger.MainActivity
import ng.inits.alphamessenger.R
import ng.inits.alphamessenger.common.UiUtils

/**
 * Created by Clement Ozemoya on 10/05/2018.
 */
class SetupViewModel(val context: Context) : BaseObservable() {
    val name = ObservableField<String>()
    val nameError = ObservableField<String?>()
    val progressVisibility = ObservableInt(View.INVISIBLE)
    val formVisibility = ObservableInt(View.VISIBLE)

    val database = FirebaseDatabase.getInstance().reference
    val user = FirebaseAuth.getInstance().currentUser

    fun save(view: View) {
        if (validateFields()) {

            UiUtils.hideKeyboard(context as Activity)
            progressVisibility.set(View.VISIBLE)
            formVisibility.set(View.INVISIBLE)

            val updates = HashMap<String, Any>()
            updates["name"] = name.get()
            Log.d(TAG, "Setting name: ${name.get()}")

            database.child("users").child(user?.uid).updateChildren(updates, { databaseError, databaseReference ->
                Log.d(TAG, "Update completed ")
                if (databaseError == null) {
                    Log.d(TAG, "Update successful, launch Main")
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } else {
                    Log.d(TAG, "Update failed", databaseError.toException())
                    Toast.makeText(context, "Something went wrong, please try again", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun validateFields() : Boolean {
        nameError.set(null)
        if (TextUtils.isEmpty(name.get())) {
            nameError.set(context.getString(R.string.error_field_required))
            return false
        }
        return true
    }

    companion object {
        val TAG: String = SetupViewModel::class.java.simpleName
    }
}

@BindingAdapter("error")
fun textError(view: TextInputLayout?, error: String?) {
    view?.error = error
}