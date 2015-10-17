package douzifly.list.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import douzifly.list.R
import douzifly.list.utils.fontSourceSansPro
import io.codetail.widget.RevealFrameLayout

/**
 * Created by air on 15/10/17.
 */
class InputPanel(context: Context, attrs: AttributeSet) : RevealFrameLayout(context, attrs) {

  val editText: EditText by lazy {
    val ed = findViewById(R.id.edit_text) as EditText
    ed.typeface = fontSourceSansPro
    ed
  }

  val colorPicker: ColorPicker by lazy {
    findViewById(R.id.color_picker) as ColorPicker
  }

  val revealView: View by lazy {
    getChildAt(0)
  }
}