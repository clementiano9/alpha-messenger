package ng.inits.alphamessenger.common

import android.app.Activity
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.regex.Pattern

/**
 * Created by Clement Ozemoya on 10/05/2018.
 */

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false) : View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun Fragment.snackbar(str: String, length: Int = Snackbar.LENGTH_SHORT) {
    rootView?.let {
        Snackbar.make(it, str, length).show()
    }
}

val Fragment.rootView
    get() = view

val Activity.rootView
    get() = this.window.decorView.findViewById<View>(android.R.id.content)

fun DateTime.getFormattedTime() : String {
    lateinit var formattedTime: String
    val now = DateTime.now()

    formattedTime = when (dayOfMonth) {
        now.dayOfMonth -> "${get12hourTime()}"
        now.dayOfMonth - 1 -> "Yesterday, ${get12hourTime()}"
        else -> "$dayOfMonth ${monthOfYear().asShortText}, $year"
    }

    return formattedTime
}

fun DateTime.get12hourTime(): String {
    val builder = DateTimeFormat.forPattern("hh:mm a")
    val b = builder.print(this).toLowerCase()
    val p = Pattern.compile("0.*")
    if (p.matcher(b).matches()) {
        return b.substring(1)
    }
    return b
}

fun String.formatTime(): String {
    val timeFormat = DateTimeFormat.forPattern("HH:mm")
    val parsedTime = timeFormat.parseDateTime(this)
    return parsedTime.get12hourTime().toUpperCase()
}

fun Long.formatTime(): String {
    val date = DateTime(this)
    return date.getFormattedTime()
}
