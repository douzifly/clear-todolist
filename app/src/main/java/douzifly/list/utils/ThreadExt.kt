package douzifly.list.utils

import java.util.concurrent.Executors

/**
 * Created by liuxiaoyuan on 2015/10/7.
 */

private val executor = Executors.newFixedThreadPool(3)

fun async(t:()->Unit) {
  executor.execute {
    t
  }
}