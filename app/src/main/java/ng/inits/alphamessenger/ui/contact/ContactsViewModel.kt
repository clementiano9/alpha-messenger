package ng.inits.alphamessenger.ui.contact

import android.databinding.BaseObservable
import android.databinding.ObservableInt
import android.view.View

/**
 * Created by Clement Ozemoya on 13/05/2018.
 */
class ContactsViewModel : BaseObservable() {

    var contentVisibility: ObservableInt = ObservableInt(View.VISIBLE)
    var progressVisibility: ObservableInt = ObservableInt(View.GONE)
    var emptyVisibility = ObservableInt(View.GONE)

    fun setLoading() {
        contentVisibility.set(View.GONE)
        progressVisibility.set(View.VISIBLE)
        emptyVisibility.set(View.GONE)
    }

    fun setEmpty(empty: Boolean) {
        if (empty) {
            progressVisibility.set(View.GONE)
            contentVisibility.set(View.GONE)
            emptyVisibility.set(View.VISIBLE)
        } else {
            progressVisibility.set(View.GONE)
            contentVisibility.set(View.VISIBLE)
            emptyVisibility.set(View.GONE)
        }
    }
}