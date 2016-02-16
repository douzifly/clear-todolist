package douzifly.list.model

import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import douzifly.list.ListApplication
import douzifly.list.R
import douzifly.list.alarm.Alarm
import douzifly.list.settings.Settings
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

    fun loadFromDb() {
        "load from db".logd(TAG)
        groups = Select().from(ThingGroup::class.java).execute()
        if (groups.size == 0) {
            // add default group and save to database
            val homeGroup = ThingGroup(ListApplication.appContext!!.resources.getString(R.string.default_list))
            homeGroup.isDefault = true
            homeGroup.creationTime = Date().time
            homeGroup.save()
            Settings.selectedGroupId = homeGroup.id
            groups.add(homeGroup)
        }

        groups.forEach {
            group ->
            loadThingsCount(group)
        }
    }

    private fun loadThings(group: ThingGroup) {
        if (group.thingsLoaded) return
        group.things.addAll(
                Select().from(Thing::class.java).where("pid=${group.id}").execute()
        )

        group.things.forEach {
            thing ->
            thing.group = group
        }

        group.thingsLoaded = true
        sort(group.things)
    }

    private fun loadThingsCount(group: ThingGroup) {
        group.inCompleteThingsCount =
                Select().from(Thing::class.java).where("pid=${group.id} and isComplete=0").count()
    }

    fun getThingsByGroupId(groupId: Long): List<Thing> {
        "getThingsByGroupId: $groupId".logd("xxxx")
        if (groupId == ThingGroup.SHOW_ALL_GROUP_ID) {
            return allThings;
        }
        val group = getGroupByGroupId(groupId)
        return group?.things ?: arrayListOf()
    }

    fun getGroupByGroupId(groupId: Long): ThingGroup? {
        groups.forEach {
            group ->
            if (group.id == groupId) {
                if (!group.thingsLoaded) {
                    loadThings(group)
                }
                return group
            }
        }
        return null
    }

    fun getThingByIdAtCurrentGroup(thingId: Long): Thing? {
        if (Settings.selectedGroupId == ThingGroup.SHOW_ALL_GROUP_ID) {
            allThings.forEach { thing ->
                if (thing.id == thingId)
                    return thing
            }
        } else {
            return getGroupByGroupId(Settings.selectedGroupId)?.findThing(thingId)
        }
        return null
    }

    private var allThings: MutableList<Thing> = arrayListOf()
        get() {
            if (field.size > 0) {
                return field
            }
            groups.forEach {
                group ->
                if (!group.thingsLoaded) {
                    loadThings(group)
                }
                "add things to all things, count: ${group.things.size} group: ${group.title}".logd("xxxx")
                field.addAll(group.things)
            }
            sort(field)
            return field
        }
        private set

    val allThingsInComplete: Int
        get() {
            return groups.map {
                group ->
                group.inCompleteThingsCount
            }.reduce { a, b -> a + b }
        }

    fun addGroup(title: String) {
        val group = ThingGroup(title)
        group.creationTime = Date().time
        group.save()
        groups.add(group)
    }

    fun release() {
        groups.clear()
    }

    fun swapThings(group: ThingGroup, fromPosition: Int, toPosition: Int): Boolean {
        val thingA = group.things[fromPosition]
        val thingB = group.things[toPosition]
        if (thingA.isComplete || thingB.isComplete) {
            return false
        }

        thingA.position = toPosition
        thingB.position = fromPosition

        group.things[fromPosition] = thingB
        group.things[toPosition] = thingA

        bg {
            thingA.save()
            thingB.save()
        }

        return true
    }

    fun addThing(group: ThingGroup, text: String, content: String, reminder: Long, color: Int, isComplete: Boolean = false) {
        val t = Thing(text, group.id, color)
        t.creationTime = Date().time
        t.reminderTime = reminder
        t.content = content
        t.position = group.things.size
        t.group = group
        t.isComplete = isComplete
        t.save()
        allThings.add(t)
        sort(allThings)
        group.things.add(t)
        group.save()
        if (!isComplete) {
            group.inCompleteThingsCount++
        }
        sort(group.things)

        if (reminder > System.currentTimeMillis()) {
            val subLen = if (t.content.length > 10) 10 else t.content.length
            val content = if (t.title.isNotBlank()) t.title else t.content.substring(0, subLen)
            Alarm.setAlarm(t.id, reminder, content, color)
        }
    }

    fun saveThing(thing: Thing, newGroup: ThingGroup?) {

        if (newGroup != null) {
            remove(thing)
            addThing(newGroup, thing.title, thing.content, thing.reminderTime, thing.color, thing.isComplete)
        } else {
            thing.save()
        }

        if (thing.reminderTime > System.currentTimeMillis()) {
            val subLen = if (thing.content.length > 10) 10 else thing.content.length
            val content = if (thing.title.isNotBlank()) thing.title else thing.content.substring(0, subLen)
            Alarm.setAlarm(thing.id, thing.reminderTime, content, thing.color)
        }
    }

    fun remove(thing: Thing) {
        val group = thing.group ?: return
        val ok = group.things.remove(thing)
        if (!ok) return
        if (!thing.isComplete) {
            group.inCompleteThingsCount--
        }
        thing.delete()
        allThings.remove(thing)
        sort(allThings)
    }

    fun removeGroup(id: Long): Boolean {
        if (id == ThingGroup.SHOW_ALL_GROUP_ID) {
            return false
        }

        groups.forEach {
            group ->
            if (group.id == id) {
                if (group.isDefault) {
                    // cant delete default
                    return false
                }

                group.things.forEach {
                    thing ->
                    allThings.remove(thing)
                }

                groups.remove(group)
                group.delete()
                Delete().from(Thing::class.java).where("pid=${group.id}").execute<Thing>()
                return true
            }
        }
        return false
    }

    fun makeComplete(thing: Thing, complete: Boolean) {
        val group = thing.group ?: return
        thing.isComplete = complete
        if (complete) {
            group.inCompleteThingsCount--
        } else {
            group.inCompleteThingsCount++
        }
        thing.save()
        sort(group.things)
        sort(allThings)
    }

    fun isShowAllGroup() = Settings.selectedGroupId == ThingGroup.SHOW_ALL_GROUP_ID

    private fun sort(things: MutableList<Thing>) {

        if (things.size < 2) {
            return
        }

        "beforeSort:".logd("MainActivity")

        things.forEach { t ->
            "${t.title} ${t.hashCode()}".logd("douzifly.list.ui.home.MainActivity")
        }

        Collections.sort(things, Comparator { t: Thing, t1: Thing ->
            return@Comparator t.compareTo(t1)
        })

        "afterSort:".logd("MainActivity")

        things.forEach { t ->
            "${t.title} iscompelte: ${t.isComplete} ${t.hashCode()}".logd("MainActivity")
        }
    }

}
