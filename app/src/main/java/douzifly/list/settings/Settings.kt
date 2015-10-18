package douzifly.list.settings

import android.content.Context
import android.content.SharedPreferences
import douzifly.list.ListApplication

/**
 * Created by air on 15/10/18.
 */

enum class Theme(val value: Int) {
  Colorful(0),
  Dot(1);

  companion object {
    fun valueOf(value: Int): Theme {
      if (value == Colorful.value) {
        return Colorful
      }
      return Dot
    }
  }
}

object Settings {

  val sp: SharedPreferences by lazy {
    ListApplication.appContext!!.getSharedPreferences("settings", Context.MODE_PRIVATE)
  }

  val K_THEME = "theme"
  val K_SOUNDS = "sounds"

  var theme: Theme = Theme.Colorful
    set(value: Theme) {
      if ($theme != value) {
        $theme = value
        sp.edit().putInt(K_THEME, value.value).commit()
      }
    }

  var sounds: Boolean = true
    set(value: Boolean) {
      if ($sounds != value) {
        $sounds = value
        sp.edit().putBoolean(K_SOUNDS, value).commit()
      }
    }


  init {
    theme = Theme.valueOf(sp.getInt(K_THEME, Theme.Colorful.value))
    sounds = sp.getBoolean(K_SOUNDS, true)
  }

}
