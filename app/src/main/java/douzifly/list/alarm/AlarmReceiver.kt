package douzifly.list.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.activeandroid.query.Select
import douzifly.list.model.Thing
import douzifly.list.notification.ClearNotification
import douzifly.list.utils.logd

/**
 * Created by air on 15/10/20.
 */
class AlarmReceiver: BroadcastReceiver() {

  override fun onReceive(context: Context?, p1: Intent?) {
    if (Intent.ACTION_BOOT_COMPLETED.equals(p1?.action)) {
      "boot complete".logd("oooo")
      Alarm.setAlarmForAllThing()
    } else {

      val thingId = p1?.getIntExtra("id", -1) ?: -1
      if (thingId == -1) {
        "no thingid".logd("oooo")
        return
      }
      // query thing
      val thing = Select().from(Thing::class.java).where("id=${thingId}").executeSingle<Thing>()
      if (thing == null) {
        "oops, no thing with id: ${thingId}".logd("oooo")
        return
      }
      if (thing.isComplete) {
        "bingo, thing already complete, id:${thingId}".logd("oooo")
        return
      }

      val title = p1?.getStringExtra("title") ?: ""
      val color = p1?.getIntExtra("color", 0x00000000) ?: 0x00000000
      "alarm time, title: ${title}".logd("oooo")
      ClearNotification.show(context!!, title, color, thingId.toInt())
    }

  }

}
