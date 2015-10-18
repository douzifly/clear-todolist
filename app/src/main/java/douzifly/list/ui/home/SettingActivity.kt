package douzifly.list.ui.home

import android.content.pm.PackageManager
import android.media.audiofx.BassBoost
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import douzifly.list.R
import douzifly.list.settings.Settings
import douzifly.list.settings.Theme
import douzifly.list.utils.colorResOf
import douzifly.list.utils.fontRailway
import douzifly.list.widget.InputPanel
import douzifly.list.widget.TitleLayout

/**
 * Created by air on 15/10/18.
 */
class SettingActivity : AppCompatActivity() {

  companion object {
    val RESULT_THEME_CHANGED = 1002
  }

  val inputPanel: TitleLayout by lazy {
    findViewById(R.id.header) as TitleLayout
  }

  val imgThemeColorSelected: ImageView by lazy {
    findViewById(R.id.img_color_selected) as ImageView
  }

  val imgThemeSimpleSelected: ImageView by lazy {
    findViewById(R.id.img_simple_selected) as ImageView
  }

  val txtSoundOnOff: TextView by lazy {
    findViewById(R.id.txt_sound_on_off) as TextView
  }

  val txtVersion: TextView by lazy {
    findViewById(R.id.txt_version) as TextView
  }

  val initTheme: Theme = Settings.theme

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.home_setting_activity)

    inputPanel.title = resources.getString(R.string.setting)
    inputPanel.txtCount.visibility = View.GONE
    inputPanel.fabSetting.visibility = View.GONE

    (findViewById(R.id.txt_copyright) as TextView).typeface = fontRailway
    (findViewById(R.id.txt_theme_title) as TextView).typeface = fontRailway
    txtSoundOnOff.typeface = fontRailway
    (findViewById(R.id.txt_sound_title) as TextView).typeface = fontRailway
    txtVersion.typeface = fontRailway
    (findViewById(R.id.txt_version_title) as TextView).typeface = fontRailway

    imgThemeColorSelected.setImageDrawable(
            GoogleMaterial.Icon.gmd_done.colorResOf(R.color.yellowPrimary)
    )

    imgThemeSimpleSelected.setImageDrawable(
            GoogleMaterial.Icon.gmd_done.colorResOf(R.color.yellowPrimary)
    )

    updateThemeSelected()
    updateSoundOnOff()

    findViewById(R.id.theme_simple_container).setOnClickListener{
      onThemeClick(Theme.Dot)
    }

    findViewById(R.id.theme_color_container).setOnClickListener {
      onThemeClick(Theme.Colorful)
    }

    txtSoundOnOff.setOnClickListener {
      onSoundsClick()
    }

    findViewById(R.id.txt_sound_title).setOnClickListener {
      onSoundsClick()
    }

    txtVersion.text = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).versionName
  }

  fun onSoundsClick() {
    Settings.sounds = !Settings.sounds
    updateSoundOnOff()
  }

  fun onThemeClick(theme: Theme) {
    if (theme == Settings.theme) {
      // not changed
      return
    }

    Settings.theme = theme
    updateThemeSelected()

    if (initTheme != theme) {
      setResult(RESULT_THEME_CHANGED)
    }

  }

  fun updateThemeSelected() {
    if (Settings.theme == Theme.Colorful) {
      imgThemeColorSelected.visibility = View.VISIBLE
      imgThemeSimpleSelected.visibility = View.GONE
    } else {
      imgThemeColorSelected.visibility = View.GONE
      imgThemeSimpleSelected.visibility = View.VISIBLE
    }
  }

  fun updateSoundOnOff() {
    txtSoundOnOff.text = if (Settings.sounds) resources.getString(R.string.on) else resources.getString(R.string.off)
  }
}