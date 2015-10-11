package douzifly.list.model

import douzifly.list.utils.logd
import java.util.*

/**
 * Created by liuxiaoyuan on 2015/10/2.
 */
object ThingsManager {

  val TAG = "ThingsManager"

  // current things
  var groups: MutableList<ThingGroup> = arrayListOf()

  var currentGroup: ThingGroup? = null
    private set


  var onDataChanged: (()->Unit)? = null

  fun loadFromDb() {
    "load from db".logd(TAG)
    groups.add(ThingGroup(0, 0, "Home", arrayListOf()))
    groups.add(ThingGroup(1, 1, "Work", arrayListOf()))

    currentGroup = groups[0]
    onDataChanged?.invoke()
  }

  fun changeGroup(id: Int) {
    if (currentGroup?.id == id) {
      // not changed
      return
    }
    groups.forEach {
      box->
      if (box.id == id) {
        currentGroup = box
        onDataChanged?.invoke()
      }
    }
  }

  fun addGroup(title: String) {
    val box = ThingGroup(-1, -1, title, arrayListOf())
    groups.add(box)
  }

  fun release() {
    onDataChanged = null
    groups.clear()
  }

  fun addThing(text: String, pid: Int, reminder: Long, color: Int) {
    val t = Thing(-1, 0, text, reminder, pid, false, color)
    currentGroup!!.things.add(t)
    sort()
    // add to db
    onDataChanged?.invoke()
  }

  fun remove(thing: Thing) {
    currentGroup!!.things?.remove(thing)
    // remove from db
    onDataChanged?.invoke()
  }

  fun removeGroup(id: Int): Boolean {

    groups.forEach {
      box->
      if (box.id == id) {
        if (groups.size() == 1) {
          // dont delete the last group
          return false
        }
        groups.remove(box)
        currentGroup = groups[0]
        onDataChanged?.invoke()
        return true
      }
    }
    return false
  }

  fun makeComplete(thing: Thing, complete: Boolean) {
    thing.isComplete = complete
    sort()
    onDataChanged?.invoke()
  }

  private fun sort() {

    val things = currentGroup!!.things

    if (things.size() < 2) {
      return
    }

    "beforeSort:".logd("MainActivity")

    things.forEach { t ->
      "${t.title} ${t.hashCode()}".logd("douzifly.list.ui.home.MainActivity")
    }

    currentGroup!!.things = things.sortedWith(object : Comparator<Thing> {
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
