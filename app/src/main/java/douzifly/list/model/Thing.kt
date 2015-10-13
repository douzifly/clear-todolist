package douzifly.list.model

import android.support.v7.internal.widget.DialogTitle
import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import java.util.*

/**
 * Created by liuxiaoyuan on 2015/10/2.
 */
/**
 * Describe a thing on list
 */
@Table(name="tbThing")
class Thing() : Model(), Comparable<Thing> {

  @Column(name="positon") var position: Int = -1
  @Column(name="title") var title: String = ""
  @Column(name="reminderAfter") var reminderAfter: Long = 0
  @Column(name="isComplete") var isComplete: Boolean = false
  @Column(name="color") var color: Int = 0
  @Column(name="pid") var pid: Long = -1

  var displayColor: Int = 0

  constructor(title: String, pid: Long, color: Int, reminderAfter: Long) : this() {
    this.title = title
    this.color = color
    this.reminderAfter = reminderAfter
    this.pid = pid
  }

  override fun compareTo(other: Thing): Int {
    if (this.isComplete && other.isComplete) return this.position - other.position
    else if (!this.isComplete && !other.isComplete) return this.position - other.position
    else if (this.isComplete) return 1
    else return -1
  }

}

@Table(name="tbGroup")
class ThingGroup() : Model() {

  constructor(title: String) : this(){
    this.title = title
  }

  @Column(name="title") var title: String = ""
  var things: ArrayList<Thing> = arrayListOf()
  @Column(name="selected") var selected: Boolean = false
  var thingsLoaded: Boolean = false
  var unCompleteThingsCount: Int = 0
}
