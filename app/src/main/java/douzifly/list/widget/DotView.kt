package douzifly.list.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
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
      field = value
      invalidate()
    }
  public var color: Int = Color.YELLOW
    set(value: Int) {
      field = value
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
    val raduisOuter = (width - paddingLeft) / 2f - 1.0f //TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.0f, resources.displayMetrics)
    paint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.0f, resources.displayMetrics)
    if (mode == Mode.Solid) {
      val raduisInner = raduisOuter - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.5f, resources.displayMetrics)
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
