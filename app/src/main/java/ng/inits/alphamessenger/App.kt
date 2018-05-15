package ng.inits.alphamessenger

import android.app.Application
import android.support.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import net.danlew.android.joda.JodaTimeAndroid

/**
 * Created by Clement Ozemoya on 08/05/2018.
 */
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        JodaTimeAndroid.init(this)
    }
}