package ng.inits.alphamessenger.ui

import android.databinding.BaseObservable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Clement Ozemoya on 10/05/2018.
 */
class SetupViewModel : BaseObservable() {
    val name = ObservableField<String>()
    val progressVisibility = ObservableInt(View.INVISIBLE)
    val formVisibility = ObservableInt(View.VISIBLE)

    fun save(view: View) {
        val database = FirebaseDatabase.getInstance().reference
        progressVisibility.set(View.VISIBLE)
        formVisibility.set(View.INVISIBLE)
    }
}