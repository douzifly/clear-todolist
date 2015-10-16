package douzifly.list.model

import android.database.Cursor
import com.activeandroid.ActiveAndroid
import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import douzifly.list.ListApplication
import douzifly.list.R
import douzifly.list.utils.logd
import douzifly.list.utils.loge
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


  var onDataChanged: (() -> Unit)? = null

  fun loadFromDb() {
    "load from db".logd(TAG)
    groups = Select().from(ThingGroup::class.java).execute()
    if (groups.size() == 0) {
      // add default group and save to database
      val homeGroup = ThingGroup(ListApplication.appContext!!.resources.getString(R.string.app_name))
      homeGroup.selected = true
      homeGroup.save()
      groups.add(homeGroup)
    }

    groups.forEach {
      group ->
      if (group.selected) {
        currentGroup = group
        loadThings(group)
      }
      loadThingsCount(group)
      onDataChanged?.invoke()
    }
  }

  private fun loadThings(group: ThingGroup) {
    if (group.thingsLoaded) return
    group.things.addAll(
            Select().from(Thing::class.java).where("pid=${group.id}").execute()
    )
    group.thingsLoaded = true
    sort(group)
  }

  private fun loadThingsCount(group: ThingGroup) {
    group.unCompleteThingsCount =
            Select().from(Thing::class.java).where("pid=${group.id} and isComplete=0").count()
  }

  fun changeGroup(id: Long) {
    if (currentGroup?.id == id) {
      // not changed
      return
    }
    groups.forEach {
      group ->
      if (group.id == id) {

        // old unselected
        val oldGroup = currentGroup
        currentGroup = group
        group.selected = true
        oldGroup?.selected = false

        loadThings(group)
        onDataChanged?.invoke()

        // save to db
        oldGroup?.save()
        group.save()
      }
    }
  }

  fun addGroup(title: String) {
    val group = ThingGroup(title)
    group.save()
    groups.add(group)
  }

  fun release() {
    onDataChanged = null
    groups.clear()
  }

  fun addThing(text: String, reminder: Long, color: Int) {
    val t = Thing(text, currentGroup!!.id, color, reminder)
    t.save()
    currentGroup!!.things.add(t)
    currentGroup!!.save()
    currentGroup!!.unCompleteThingsCount++
    sort(currentGroup!!)
    // add to db
    onDataChanged?.invoke()
  }

  fun remove(thing: Thing) {
    currentGroup!!.things?.remove(thing)
    currentGroup!!.unCompleteThingsCount--
    thing.delete()
    // remove from db
    onDataChanged?.invoke()
  }

  fun removeGroup(id: Long): Boolean {

    groups.forEach {
      box ->
      if (box.id == id) {
        if (groups.size() == 1) {
          // dont delete the last group
          return false
        }
        groups.remove(box)
        currentGroup = groups[0]
        currentGroup!!.selected = true
        onDataChanged?.invoke()
        box.delete()
        currentGroup!!.save()

        Delete().from(Thing::class.java).where("pid=${box.id}").execute<Thing>()
        return true
      }
    }
    return false
  }

  fun makeComplete(thing: Thing, complete: Boolean) {
    thing.isComplete = complete
    if (complete) {
      currentGroup!!.unCompleteThingsCount--
    } else {
      currentGroup!!.unCompleteThingsCount++
    }
    thing.save()
    sort(currentGroup!!)
    onDataChanged?.invoke()
  }

  private fun sort(group: ThingGroup) {

    val things = group.things

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

    }) as ArrayList<Thing>

    "afterSort:".logd("MainActivity")

    things.forEach { t ->
      "${t.title} iscompelte: ${t.isComplete} ${t.hashCode()}".logd("MainActivity")
    }
  }

}
