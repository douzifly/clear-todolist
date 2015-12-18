package douzifly.list.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import douzifly.list.R
import java.util.*

/**
 * Created by liuxiaoyuan on 2015/10/7.
 */
class ColorPicker(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    companion object {

        fun getDimedColor(originColor: Int, dimFactor: Float = 0.94f): Int {
            val red = Color.red(originColor).toFloat()
            val green = Color.green(originColor).toFloat()
            val blue = Color.blue(originColor).toFloat()
            return Color.rgb((red * dimFactor).toInt(), (green * dimFactor).toInt(), (blue * dimFactor).toInt())
        }

    }

    // 每一个颜色都是一个声音
    val colors: Array<Long> = arrayOf(
            0xff4285f4, // google blue
            0xffec4536, // google brick red
            0xfffbbd06, // google yellow
            0xff34a852, // google green
            0xffee468b, // pink
            0xff3d7685, // steel blue
            0xff572704, // chocolate
            0xffe8d3a3  // tan
    )

    inline fun randomColor(): Int = colors[Random(System.currentTimeMillis()).nextInt(colors.size)].toInt()

    var selectedColor: Int = colors[0].toInt()
        private set(color: Int) {
            field = color
        }

    var selectItem: Item? = null

    init {

        orientation = LinearLayout.HORIZONTAL
        setGravity(Gravity.CENTER_VERTICAL)

        colors.forEach {
            colorL ->
            val color = colorL.toInt()
            val view = LayoutInflater.from(context).inflate(R.layout.color_picker_item, this, false)
            val item = Item(view, color)
            addView(view)

            view.tag = item
            view.setOnClickListener {
                v ->
                val i = v.tag as Item
                setSelected(i)
            }
        }
    }

    fun setSelected(item: Item) {
        if (item == selectItem) {
            return
        }

        selectItem?.selected = false
        item.selected = true
        selectItem = item
        selectedColor = item.color
    }

    fun setSelected(color: Int) {
        val count = childCount
        for (i in 0..count-1) {
            val item = getChildAt(i).tag as Item
            if (color == item.color) {
                setSelected(item)
                break;
            }
        }
    }

    inner class Item(
            val view: View,
            val color: Int
    ) {

        init {
            view.setBackgroundColor(color)
        }

        var selected: Boolean = false
            set(value: Boolean) {
                if (field == value) return
                field = value
                updateUI()
            }

        private fun updateUI() {
            var scale = if (selected) 1.3f else 1.0f

            val animatorW = ObjectAnimator.ofFloat(view, "scaleX", scale)
            val animatorH = ObjectAnimator.ofFloat(view, "scaleY", scale)

            val animators = AnimatorSet()
            animators.playTogether(animatorH, animatorW)
            animators.setDuration(150)
            animators.interpolator = AccelerateInterpolator()
            animators.start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (childCount > 0) {
            val child0 = getChildAt(0)
            val h = child0.height * 1.6f
            setMeasuredDimension(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(h.toInt(), View.MeasureSpec.EXACTLY))
        }

    }
}