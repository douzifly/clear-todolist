package douzifly.list.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

/**
 * Created by air on 15/10/17.
 */

class DotView(context: Context, attrs: AttributeSet) : View(context, attrs) {

  public enum class Mode {
    Hollow,
    Solid,
    Done
  }

  public var mode: Mode = Mode.Hollow
    set (value: Mode) {
      $mode = value
      invalidate()
    }
  public var color: Int = Color.YELLOW
    set(value: Int) {
      $color = value
      invalidate()
    }

  public var colorRes: Int = 0
    set(value: Int) {
      color = context.resources.getColor(value)
    }

  public var doneDrawable: Drawable? = null

  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

  override fun onDraw(canvas: Canvas) {
    if (mode == Mode.Done) {
      val a = 5
      doneDrawable?.bounds = Rect(width / a, height / a, width - width / a, height - height / a)
      doneDrawable?.draw(canvas)
      return
    }
    paint.color = color
    val cx = width / 2f
    val cy = height / 2f
    val raduisOuter = (width - paddingLeft) / 2f - 5.0f
    paint.strokeWidth = 3.0f
    if (mode == Mode.Solid) {
      val raduisInner = (width - paddingLeft) / 3.5f
      paint.style = Paint.Style.STROKE
      canvas.drawCircle(cx, cy, raduisOuter, paint)
      paint.style = Paint.Style.FILL
      canvas.drawCircle(cx, cy, raduisInner, paint)
    } else {
      paint.style = Paint.Style.STROKE
      canvas.drawCircle(cx, cy, raduisOuter, paint)
    }
  }
}
