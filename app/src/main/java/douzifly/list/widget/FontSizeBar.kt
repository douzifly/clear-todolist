package douzifly.list.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import douzifly.list.ListApplication
import douzifly.list.R

/**
 * Created by douzifly on 2/3/16.
 */
class FontSizeBar(context: Context, attrs: AttributeSet)
: RelativeLayout(context, attrs) ,
SeekBar.OnSeekBarChangeListener {

    companion object {
        val FONT_SMALL = 0
        val FONT_NORMAL = 1
        val FONT_LARGE = 2

        fun fontSizeToDp(fontSize:Int):Float {
            when(fontSize) {
                FONT_SMALL -> return smallFontSize
                FONT_NORMAL -> return normalFontSize
                FONT_LARGE -> return largeFontSize
            }
            return normalFontSize
        }

        val smallFontSize: Float by lazy {
            ListApplication.appContext!!.resources.getDimension(R.dimen.txt_size_small)
        }

        val normalFontSize: Float by lazy {
            ListApplication.appContext!!.resources.getDimension(R.dimen.txt_size_big)
        }

        val largeFontSize: Float by lazy {
            ListApplication.appContext!!.resources.getDimension(R.dimen.txt_size_extra)
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        val progress = seekBar!!.progress
        if (progress > 75) {
            seekBar.progress = 100
        } else if (progress > 50 ) {
            seekBar.progress = 50
        } else if (progress > 25) {
            seekBar.progress = 50
        } else {
            seekBar.progress = 0
        }
        calcFontSize()
        fontSizeChangeListener?.invoke(fontSize)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    fun calcFontSize() {
        if (seekBar.progress < 50) {
            fontSize = FONT_SMALL
        } else if (seekBar.progress == 50) {
            fontSize = FONT_NORMAL
        } else {
            fontSize = FONT_LARGE
        }
    }

    val txtSmall: TextView by lazy {
        findViewById(R.id.txt_small) as TextView
    }

    val txtNormal: TextView by lazy {
        findViewById(R.id.txt_normal) as TextView
    }

    val txtLarge: TextView by lazy {
        findViewById(R.id.txt_large) as TextView
    }

    val seekBar: SeekBar by lazy {
        findViewById(R.id.seek_bar) as SeekBar
    }

    var fontSizeChangeListener: ((Int) -> Unit)? = null

    var fontSize: Int = FONT_SMALL
        set(value) {
            if (field == value) {
                return
            }
            field = value
            if (field == FONT_SMALL) {
                seekBar.progress = 0
            } else if (field == FONT_NORMAL) {
                seekBar.progress = 50
            } else if (field == FONT_LARGE) {
                seekBar.progress = 100
            }
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.font_size_bar, this, true)
        seekBar.setOnSeekBarChangeListener(this)
        seekBar.max = 100
    }


}