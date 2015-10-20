package douzifly.list.sounds

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import douzifly.list.ListApplication
import douzifly.list.R
import douzifly.list.settings.Settings
import douzifly.list.utils.logd
import java.util.*

/**
 * Created by air on 15/10/19.
 */
object Sound {

  val S_DELETE = 0
  val S_CLICK_ADD = 1
  val S_CLICK_DONE = 2
  val S_CLICK_ITEM = 3
  val S_DONE = 4

  val map: HashMap<Int, Int> = HashMap()

  var sp: SoundPool

  init {
    sp = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
  }

  fun load(context: Context) {
    map.put(S_CLICK_ADD, sp.load(context.assets.openFd("sounds/click_add.ogg"), 1))
    map.put(S_CLICK_DONE, sp.load(context.assets.openFd("sounds/click_done.ogg"), 1))
    map.put(S_CLICK_ITEM, sp.load(context.assets.openFd("sounds/click_item.ogg"), 1))
    map.put(S_DELETE, sp.load(context.assets.openFd("sounds/delete.ogg"), 1))
    map.put(S_DONE, sp.load(context.assets.openFd("sounds/done.ogg"), 1))
  }

  fun play(id: Int) {
    if (!Settings.sounds) {
      return
    }
    val vol = 0.99f
    var sid = map.get(id)!!
    sp.play(sid, vol, vol, 0, 0, 1f)
  }

}