package douzifly.list.model

import douzifly.list.utils.logd
import java.util.*

/**
 * Created by liuxiaoyuan on 2015/10/2.
 */
object ThingsManager {

  var things: MutableList<Thing> = arrayListOf()

  var onDataChanged: (()->Unit)? = null

  fun loadFromDb() {

  }

  fun release() {
    onDataChanged = null
    things.clear()
  }

  fun add(text: String, pid: Int, reminder: Long, color: Int) {
    val t = Thing(-1, 0, text, reminder, pid, false, color)
    things.add(t)
    sort()
    // add to db
    onDataChanged?.invoke()
  }

  fun remove(thing: Thing) {
    things.remove(thing)
    // remove from db
    onDataChanged?.invoke()
  }

  fun makeComplete(thing: Thing, complete: Boolean) {
    thing.isComplete = complete
    sort()
    onDataChanged?.invoke()
  }

  private fun sort() {

    if (things.size() < 2) {
      return
    }

    "beforeSort:".logd("MainActivity")

    things.forEach { t ->
      "${t.title} ${t.hashCode()}".logd("douzifly.list.MainActivity")
    }

    things = things.sortedWith(object : Comparator<Thing> {
      override fun compare(p0: Thing?, p1: Thing?): Int {
        return p0!!.compareTo(p1!!)
      }

    }) as MutableList<Thing>

    "afterSort:".logd("MainActivity")

    things.forEach { t ->
      "${t.title} iscompelte: ${t.isComplete} ${t.hashCode()}".logd("MainActivity")
    }
  }

}
