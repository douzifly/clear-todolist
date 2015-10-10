package douzifly.list.model

/**
 * Created by liuxiaoyuan on 2015/10/2.
 */
/**
 * Describe a thing on list
 */
class Thing(
    var id: Int,
    var index: Int,
    var title: String,
    var reminderAfter: Long,
    var groupId: Int,
    var isComplete: Boolean = false,
    var color: Int
) : Comparable<Thing> {
  override fun compareTo(other: Thing): Int {
    if (this.isComplete && other.isComplete) return this.index - other.index
    else if (!this.isComplete && !other.isComplete) return this.index - other.index
    else if (this.isComplete) return 1
    else return -1
  }

}

data class ThingGroup(
    var id: Int,
    var index: Int,
    var title: String,
    var things: MutableList<Thing>
)
