package douzifly.list.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

/**
 * Created by liuxiaoyuan on 2015/10/7.
 */

private val executor = Executors.newFixedThreadPool(3)
private val handler: Handler by lazy {
  Handler(Looper.getMainLooper())
}


fun bg(t:()->Unit) {
  executor.execute {
    t()
  }
}

fun ui(t:()->Unit) {
  handler.post {
    t()
  }
}

fun ui(delay: Long, t:()->Unit) {
  handler.postDelayed(t, delay)
}
