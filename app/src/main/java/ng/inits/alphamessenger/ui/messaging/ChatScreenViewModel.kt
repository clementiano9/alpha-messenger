package ng.inits.alphamessenger.ui.messaging

import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.util.Log
import android.view.View

/**
 * Created by Clement Ozemoya on 14/05/2018.
 */
class ChatScreenViewModel : BaseObservable() {
    var progressVisibility = ObservableInt(View.GONE)
    var message = ObservableField<String>()

    fun sendClicked(view: View) {
        Log.d(TAG, "Send clicked: ${message.get()}")
    }

    companion object {
        val TAG: String = ChatScreenViewModel::class.java.simpleName
    }
}