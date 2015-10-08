package douzifly.list.utils

import android.util.Log

/**
 * Created by liuxiaoyuan on 2015/10/6.
 */

fun String.logd(tag: String) {
  if (DEBUG) {
    Log.d(tag, this)
  }
}

fun String.logw(tag: String) {
  if (DEBUG) {
    Log.w(tag, this)
  }
}

fun String.loge(tag: String) {
  if (DEBUG) {
    Log.e(tag, this)
  }
}

fun String.logv(tag: String) {
  if (DEBUG) {
    Log.v(tag, this)
  }
}

fun String.logi(tag: String) {
  if (DEBUG) {
    Log.i(tag, this)
  }
}