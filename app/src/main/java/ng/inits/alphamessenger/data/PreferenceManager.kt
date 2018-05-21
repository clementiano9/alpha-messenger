package ng.inits.alphamessenger.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Clement Ozemoya on 11/05/2018.
 */
class PreferenceManager(val context: Context) {

    var preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE)
    }

    fun hasPassedSetup() : Boolean = preferences.getBoolean(PASSED_SETUP, false)

    fun markPassedSetup() = preferences.edit().putBoolean(PASSED_SETUP, true).apply()

    companion object {
        val TAG: String = PreferenceManager::class.java.simpleName
        private const val PREF_FILENAME = "alphamessenger_pref_file"
        private const val PASSED_SETUP = "passed_setup"
    }
}