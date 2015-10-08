package douzifly.list

import android.app.Application
import android.content.Context

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
  }
}