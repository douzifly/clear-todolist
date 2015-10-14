package douzifly.list

import android.content.Context
import com.activeandroid.ActiveAndroid
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
    ActiveAndroid.initialize(this)
  }
}