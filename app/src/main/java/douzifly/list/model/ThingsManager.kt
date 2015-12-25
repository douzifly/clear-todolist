package douzifly.list.model

import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import douzifly.list.ListApplication
import douzifly.list.R
import douzifly.list.alarm.Alarm
import douzifly.list.utils.bg
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


    private var onDataChanged: MutableList<() -> Unit> = ArrayList()

    fun addListener(l: () -> Unit)  = onDataChanged.add(l)

    fun removeListener(l: ()-> Unit) = onDataChanged.remove(l)

    fun notifyListeners() {
        bg {
            onDataChanged.forEach {
                l ->
                l.invoke()
            }
        }
    }

    fun loadFromDb() {
        "load from db".logd(TAG)
        groups = Select().from(ThingGroup::class.java).execute()
        if (groups.size() == 0) {
            // add default group and save to database
            val homeGroup = ThingGroup(ListApplication.appContext!!.resources.getString(R.string.default_list))
            homeGroup.selected = true
            homeGroup.isDefault = true
            homeGroup.creationTime = Date().time
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
        }
        notifyListeners()
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
                notifyListeners()

                // save to db
                oldGroup?.save()
                group.save()
            }
        }
    }

    fun addGroup(title: String) {
        val group = ThingGroup(title)
        group.creationTime = Date().time
        group.save()
        groups.add(group)
    }

    fun release() {
        onDataChanged.clear()
        groups.clear()
    }

    fun addThing(text: String, content: String, reminder: Long, color: Int) {
        val t = Thing(text, currentGroup!!.id, color)
        t.creationTime = Date().time
        t.reminderTime = reminder
        t.content = content
        t.save()
        currentGroup!!.things.add(t)
        currentGroup!!.save()
        currentGroup!!.unCompleteThingsCount++
        sort(currentGroup!!)
        // add to db
        notifyListeners()

        if (reminder > System.currentTimeMillis()) {
            Alarm.setAlarm(t.id, reminder, text, color)
        }
    }

    fun saveThing(thing: Thing) {
        thing.save()
        notifyListeners()
    }

    fun remove(thing: Thing) {
        val ok = currentGroup!!.things.remove(thing)
        if (!ok) return
        if (!thing.isComplete) {
            currentGroup!!.unCompleteThingsCount--
        }
        thing.delete()
        // remove from db
        notifyListeners()
    }

    fun removeGroup(id: Long): Boolean {

        groups.forEach {
            group ->
            if (group.id == id) {
                if (group.isDefault) {
                    // cant delete default
                    return false
                }

                groups.remove(group)
                currentGroup = groups[0]
                currentGroup!!.selected = true
                notifyListeners()
                group.delete()
                currentGroup!!.save()

                Delete().from(Thing::class.java).where("pid=${group.id}").execute<Thing>()
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
        notifyListeners()
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

//        currentGroup!!.things = things.sortedWith(object : Comparator<Thing> {
//            override fun compare(p0: Thing?, p1: Thing?): Int {
//                return p0!!.compareTo(p1!!)
//            }
//
//        }) as ArrayList<Thing>

        Collections.sort(currentGroup!!.things, Comparator { t: Thing, t1: Thing ->
            return@Comparator  t.compareTo(t1)
        })

        "afterSort:".logd("MainActivity")

        things.forEach { t ->
            "${t.title} iscompelte: ${t.isComplete} ${t.hashCode()}".logd("MainActivity")
        }
    }

}
