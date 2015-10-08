package douzifly.list.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import douzifly.list.R

/**
 * Created by liuxiaoyuan on 2015/9/30.
 */
public class DateHeader(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

  override fun onFinishInflate() {

    (findViewById(R.id.txt_date) as TextView).text = "Monday"

  }


}