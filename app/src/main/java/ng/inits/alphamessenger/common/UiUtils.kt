package ng.inits.alphamessenger.common

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

/**
 * Created by Clement Ozemoya on 11/05/2018.
 */
class UiUtils {

    companion object {
        fun hideKeyboard(activity: Activity) {
            val service = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            service.hideSoftInputFromWindow(activity.currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}