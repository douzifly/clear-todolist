package douzifly.list.model

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import douzifly.list.ListApplication
import douzifly.list.R
import douzifly.list.settings.Settings
import java.util.*

/**
 * Created by liuxiaoyuan on 2015/10/2.
 */
/**
 * Describe a thing on list
 */
@Table(name = "tbThing")
class Thing() : Model(), Comparable<Thing> {

    @Column(name = "positon") var position: Int = -1
    @Column(name = "title") var title: String = ""
    @Column(name = "reminderTime") var reminderTime: Long = 0
    @Column(name = "isComplete") var isComplete: Boolean = false
    @Column(name = "color") var color: Int = 0
    @Column(name = "pid") var pid: Long = -1
    @Column(name = "creation") var creationTime: Long = 0
    @Column(name = "content") var content: String = ""

    var displayColor: Int = 0
    var group: ThingGroup? = null

    constructor(title: String, pid: Long, color: Int) : this() {
        this.title = title
        this.color = color
        this.pid = pid
    }

    override fun compareTo(other: Thing): Int {
        if (this.isComplete && other.isComplete) return this.position - other.position
        else if (!this.isComplete && !other.isComplete) return this.position - other.position
        else if (this.isComplete) return 1
        else return -1
    }

    override fun toString(): String {
        return "title:$title position:$position"
    }

}

@Table(name = "tbGroup")
class ThingGroup() : Model() {

    companion object  {
        val SHOW_ALL_GROUP_ID = -1L
    }


    constructor(title: String) : this() {
        this.title = title
    }

    @Column(name = "title") var title: String = ""
        get() {
            return if (isDefault) ListApplication.appContext?.getString(R.string.default_group_title) ?: field else field
        }
    var things: ArrayList<Thing> = arrayListOf()
    @Column (name = "selected") @Deprecated("use Setings.selectedGroupId") var selected: Boolean = false
    @Column(name = "is_default") var isDefault: Boolean = false
    @Column(name = "creation") var creationTime: Long = 0

    var thingsLoaded: Boolean = false
    var inCompleteThingsCount: Int = 0

    fun findThing(id: Long): Thing? {
        return things.find {
            t ->
            t.id == id
        }
    }

    fun clearAllDisplayColor() {
        things.forEach {
            thing ->
            thing.displayColor = 0
        }
    }

    fun isGroupSelected():Boolean {
        return Settings.selectedGroupId == id
    }
}
