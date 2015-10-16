package douzifly.list.utils

import android.graphics.Typeface
import douzifly.list.ListApplication

/**
 * Created by liuxiaoyuan on 2015/10/7.
 */

val fontRailway by lazy {
  Typeface.createFromAsset(ListApplication.appContext!!.assets, "fonts/Railway.ttf")
}

val fontAlegreya by lazy {
  Typeface.createFromAsset(ListApplication.appContext!!.assets, "fonts/alegreya.ttf")
}

val fontSourceSansPro by lazy {
  Typeface.createFromAsset(ListApplication.appContext!!.assets, "fonts/SourceSansPro.ttf")
}