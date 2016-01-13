package douzifly.list

import android.content.Context
import com.activeandroid.ActiveAndroid
import com.activeandroid.Configuration
import com.activeandroid.app.Application

/**
 * Created by liuxiaoyuan on 2015/10/7.
 */
class ListApplication : Application() {

    companion object {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        val dbName = "list.db"
        val config = Configuration.Builder(this).setDatabaseName(dbName).create()
        ActiveAndroid.initialize(config)
    }
}