package ng.inits.alphamessenger.ui.chat

import android.databinding.BaseObservable
import android.databinding.ObservableInt
import android.view.View

/**
 * Created by Clement Ozemoya on 12/05/2018.
 */
class ChatsViewModel : BaseObservable() {

    var contentVisibility: ObservableInt = ObservableInt(View.VISIBLE)
    var progressVisibility: ObservableInt = ObservableInt(View.GONE)
    var emptyVisibility = ObservableInt(View.GONE)

    fun fetchChats() {
        progressVisibility.set(View.VISIBLE)
        contentVisibility.set(View.GONE)
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