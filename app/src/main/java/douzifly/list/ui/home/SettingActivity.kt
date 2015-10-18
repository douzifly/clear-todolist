package douzifly.list.ui.home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import douzifly.list.R
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.home_setting_activity)

    inputPanel.title = resources.getString(R.string.setting)
    inputPanel.txtCount.visibility = View.GONE
    inputPanel.fabSetting.visibility = View.GONE

    (findViewById(R.id.txt_copyright) as TextView).typeface = fontRailway
  }
}