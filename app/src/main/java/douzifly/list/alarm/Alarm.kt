package douzifly.list.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.activeandroid.query.Select
import douzifly.list.ListApplication
import douzifly.list.model.Thing
import douzifly.list.utils.logd

/**
 * Created by air on 15/10/20.
 */
object  Alarm {

  fun setAlarmForAllThing() {
    val things = Select()
                  .from(Thing::class.java)
                  .where("isComplete=0 and reminderTime>0").execute<Thing>()

    things.forEach {
      thing->
      if (thing.reminderTime > System.currentTimeMillis()) {
        setAlarm(thing.id, thing.reminderTime, thing.title, thing.color)
      }
    }
  }

  fun setAlarm(thingId: Long, reminderTime: Long, title: String, color: Int) {
    "setAlarm id: ${thingId} time:${reminderTime} title:${title}".logd("oooo")
    val am = ListApplication.appContext!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val i = Intent("ClearAlarmPro")
    i.putExtra("title", title)
    i.putExtra("color", color)
    i.putExtra("id", thingId.toInt())
    val pi = PendingIntent.getBroadcast(ListApplication.appContext, thingId.toInt(), i, PendingIntent.FLAG_CANCEL_CURRENT)
    am.set(AlarmManager.RTC_WAKEUP, reminderTime, pi)
  }

}