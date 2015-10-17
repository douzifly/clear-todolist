package douzifly.list.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import douzifly.list.R
import douzifly.list.utils.fontRailway

/**
 * Created by air on 15/10/17.
 */
class TitleLayout(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

  private val txtTitle: TextView by lazy {
    findViewById(R.id.txt_title) as TextView
  }

  private val txtCount: TextView by lazy {
    findViewById(R.id.txt_count) as TextView
  }

  public var title: String
    get() {
      return txtTitle.text.toString()
    }
    set(value: String) {
      txtTitle.setText(value)
    }

  public var count: Int = 0
    set(value: Int) {
      txtCount.text = value.toString()
      $count = value
    }

  public var titleClickListener: (()-> Unit)? = null

  override fun onFinishInflate() {
    super.onFinishInflate()
    txtTitle.typeface = fontRailway
    txtTitle.setOnClickListener {
      titleClickListener?.invoke()
    }
  }
}
