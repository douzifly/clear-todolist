package douzifly.list.utils

import android.graphics.Typeface
import douzifly.list.ListApplication

/**
 * Created by liuxiaoyuan on 2015/10/7.
 */

val fontRailway by lazy {
  Typeface.createFromAsset(ListApplication.appContext!!.assets, "Railway.ttf")
}

val fontAlegreya by lazy {
  Typeface.createFromAsset(ListApplication.appContext!!.assets, "alegreya.ttf")
}

val fontSourceSansPro by lazy {
  Typeface.createFromAsset(ListApplication.appContext!!.assets, "SourceSansPro.ttf")
}