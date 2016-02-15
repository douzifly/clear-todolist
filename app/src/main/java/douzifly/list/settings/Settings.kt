package douzifly.list.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import douzifly.list.ListApplication
import douzifly.list.utils.bg
import douzifly.list.widget.FontSizeBar

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
    val K_FONT_SIZE = "font_size"
    val K_SELECTED_GROUP_ID = "selected_group_id"
    val INVALID_GROUP_ID = -1L

    val APP_DIR: String by lazy {
        try {
            "${Environment.getExternalStorageDirectory().path}/clearlist/"
        } catch(e: Exception) {
            ""
        }
    }

    val BACKUP_DIR: String by lazy {
        val appDir = APP_DIR
        if (appDir.isEmpty()) {
            ""
        } else {
            "${appDir}backup/"
        }
    }

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
                bg {
                    sp.edit().putBoolean(K_SOUNDS, value).commit()
                }
            }
        }

    var randomColor: Boolean = true
        set(value: Boolean) {
            if (field != value) {
                field = value
                bg {
                    sp.edit().putBoolean(K_RANDOM_COLOR, value).commit()
                }
            }
        }
    var fontSize: Int = FontSizeBar.FONT_NORMAL
        set(value: Int) {
            if (field != value) {
                field = value
                bg {
                    sp.edit().putInt(K_FONT_SIZE, value).commit()
                }
            }
        }

    var selectedGroupId: Long = INVALID_GROUP_ID
        set(value: Long) {
            if (field != value) {
                field = value
                bg {
                    sp.edit().putLong(K_SELECTED_GROUP_ID, value).commit()
                }
            }
        }

    init {
        theme = Theme.valueOf(sp.getInt(K_THEME, Theme.Colorful.value))
        //    theme = Theme.Colorful
        sounds = sp.getBoolean(K_SOUNDS, true)
        randomColor = sp.getBoolean(K_RANDOM_COLOR, true)
        fontSize = sp.getInt(K_FONT_SIZE, FontSizeBar.FONT_NORMAL)
        selectedGroupId = sp.getLong(K_SELECTED_GROUP_ID, INVALID_GROUP_ID)
    }

}
