package douzifly.list.settings

import android.content.Context
import android.content.SharedPreferences
import douzifly.list.ListApplication
import douzifly.list.utils.bg

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
  val K_RANDOM_COLOR = "random_color"

  var theme: Theme = Theme.Colorful
    set(value: Theme) {
      if (field != value) {
        field = value
        bg {
          sp.edit().putInt(K_THEME, value.value).commit()
        }
      }
    }

  var sounds: Boolean = true
    set(value: Boolean) {
      if (field != value) {
        field = value
        bg  {
          sp.edit().putBoolean(K_SOUNDS, value).commit()
        }
      }
    }

  var randomColor: Boolean = true
    set(value: Boolean) {
      if (field != value) {
        field = value
        bg  {
          sp.edit().putBoolean(K_RANDOM_COLOR, value).commit()
        }
      }
    }


  init {
    theme = Theme.valueOf(sp.getInt(K_THEME, Theme.Colorful.value))
//    theme = Theme.Colorful
    sounds = sp.getBoolean(K_SOUNDS, true)
    randomColor = sp.getBoolean(K_RANDOM_COLOR, true)
  }

}
