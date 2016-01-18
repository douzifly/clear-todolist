package douzifly.list.ui.home

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.clans.fab.FloatingActionButton
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import douzifly.list.R
import douzifly.list.model.Thing
import douzifly.list.model.ThingsManager
import douzifly.list.model.randomEmptyText
import douzifly.list.settings.Settings
import douzifly.list.settings.Theme
import douzifly.list.sounds.Sound
import douzifly.list.utils.*
import douzifly.list.widget.*
import java.util.*
import kotlin.collections.size
import kotlin.text.isBlank
import kotlin.text.trim

public class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "MainActivity"
        val REQ_SETTING = 1
        val REQ_DETAIL = 2
        val REQ_EDIT_GROUP = 3
    }

    val mRecyclerView: RecyclerView by lazy {
        findViewById(R.id.recycler_view) as RecyclerView
    }

    val mFabButton: FloatingActionButton by lazy {
        findViewById(R.id.fab_add) as FloatingActionButton
    }

    val mInputPanel: InputPanel by lazy {
        findViewById(R.id.input_panel) as InputPanel
    }

    val mTxtEmpty: View by lazy {
        findViewById(R.id.txt_empty)
    }

    val mTitleLayout: TitleLayout by lazy {
        findViewById(R.id.header) as TitleLayout
    }

    val mActionPanel: ActionPanel by lazy {
        findViewById(R.id.action_panel) as ActionPanel
    }

    val mFabSetting: FloatingActionButton by lazy {
        findViewById(R.id.fab_setting) as FloatingActionButton
    }

    val dataListener = {
        ui {
            checkShowEmptyText()
            updateTitle()
            (mRecyclerView.adapter as ThingsAdapter).things = ThingsManager.currentGroup?.things
            mTitleLayout.count = ThingsManager.currentGroup?.unCompleteThingsCount ?: 0
        }
    }

    val mFabListener: (v: View) -> Unit = {

        if (mInputPanel.visibility == View.VISIBLE) {

            val cx = (mFabButton.left + mFabButton.right) / 2
            val cy = mFabButton.top + mFabButton.height / 2
            startCircularReveal(cx, cy, mInputPanel.revealView, true) {
                mInputPanel.visibility = View.INVISIBLE
                setFabAsCommit(false)
                ui {
                    mInputPanel.editText.hideKeyboard()
                    ui(100) {
                        handleInputDone()
                        mInputPanel.reset()
                        Sound.play(Sound.S_CLICK_DONE)
                    }
                }
            }

        } else {
            val cx = (mFabButton.left + mFabButton.right) / 2
            val cy = mFabButton.top + mFabButton.height / 2
            mInputPanel.visibility = View.VISIBLE
            startCircularReveal(cx, cy, mInputPanel.revealView, false) {
                mInputPanel.editText.requestFocus()
                mInputPanel.editText.showKeyboard()
            }
            setFabAsCommit(true)

            Sound.play(Sound.S_CLICK_ADD)
        }
    }

    fun checkShowEmptyText() {
        mTxtEmpty.visibility = if (ThingsManager.currentGroup?.things?.size() == 0) View.VISIBLE else View.GONE
        if (mTxtEmpty.visibility == View.VISIBLE) {
            (mTxtEmpty as TextView).text = randomEmptyText()
        }
    }

    fun handleInputDone() {
        val textString = mInputPanel.editText.text.toString().trim()
        val contentString = mInputPanel.contentEditText.text.toString()
        if (textString.isBlank() && contentString.isBlank()) {
            return
        }

        ThingsManager.addThing(textString, contentString
                ,mInputPanel.reminderDate?.time ?: -1, mInputPanel.colorPicker.selectedColor)
    }

    fun setFabAsCommit(asCommit: Boolean) {
        if (asCommit) {
            mFabButton.setImageDrawable(
                    GoogleMaterial.Icon.gmd_done.colorResOf(R.color.redPrimary)
            )
            mFabButton.setColorNormalResId(R.color.whitePrimary)
            mFabButton.setColorPressedResId(R.color.whitePressed)
            mFabButton.setColorRippleResId(R.color.whiteRipple)
        } else {
            mFabButton.setImageDrawable(
                    GoogleMaterial.Icon.gmd_add.colorOf(Color.WHITE)
            )
            mFabButton.setColorNormalResId(R.color.redPrimary)
            mFabButton.setColorPressedResId(R.color.redPressed)
            mFabButton.setColorRippleResId(R.color.redRipple)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFabButton.setOnClickListener(mFabListener)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = ThingsAdapter()
        (mTxtEmpty as TextView).typeface = fontAlegreya

        setFabAsCommit(false)
        checkShowEmptyText()

        mTitleLayout.titleClickListener = {
            // click title, show box
            startActivityForResult(Intent(this, GroupEditorActivity::class.java), REQ_EDIT_GROUP)
        }

        mActionPanel.onDeleteListener = {
            thing ->
            doDelete(thing)
            Sound.play(Sound.S_DELETE)
        }

        mActionPanel.onDoneListener = {
            thing ->
            doDone(thing)
            Sound.play(Sound.S_DONE)
        }

        mActionPanel.onHide = {
            mFabButton.visibility = View.VISIBLE
        }

        mFabSetting.setOnClickListener {
            startActivityForResult(Intent(this, SettingActivity::class.java), REQ_SETTING,
                    ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, mFabSetting , "st").toBundle())
        }

        ThingsManager.addListener(dataListener)
        bg {
            ThingsManager.loadFromDb()
        }
        Sound.load(this)
    }

    private fun updateTitle() {
        if (ThingsManager.currentGroup?.isDefault ?: false) {
            mTitleLayout.title = getString(R.string.default_list)
        } else {
            mTitleLayout.title = ThingsManager.currentGroup?.title ?: ""
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //
        if (REQ_SETTING == requestCode && resultCode == SettingActivity.RESULT_THEME_CHANGED) {
            mRecyclerView.adapter.notifyDataSetChanged()
        } else if (REQ_DETAIL == requestCode) {
            val id = data?.extras?.getLong(DetailActivity.EXTRA_THING_ID) ?: -1L
            var thing: Thing? = null
            if (id > 0) {
                thing = ThingsManager.currentGroup?.findThing(id)
            }
            if (thing == null) return
            if (resultCode == DetailActivity.RESULT_DONE) {
                doDone(thing)
            } else {
                doDelete(thing)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ThingsManager.removeListener(dataListener)
        ThingsManager.release()
    }

    fun startCircularReveal(cx: Int, cy: Int, viewRoot: View, reverse: Boolean, end: (() -> Unit)? = null) {
        val endRadius = Math.max(viewRoot.width, viewRoot.height).toFloat()
        val startRadius = if (reverse) endRadius else 0f
        val finalRadius = if (reverse) 0f else endRadius
        val anim = io.codetail.animation.ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, startRadius, finalRadius)
        anim.setDuration(200)
        anim.addListener(object : io.codetail.animation.SupportAnimator.AnimatorListener {
            override fun onAnimationRepeat() {
            }

            override fun onAnimationCancel() {
            }

            override fun onAnimationEnd() {
                end?.invoke()
            }

            override fun onAnimationStart() {
            }

        })
        anim.start()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mInputPanel.visibility == View.VISIBLE) {
                mFabListener.invoke(mFabButton)
                return false
            }
            if (mActionPanel.isShowing) {
                mActionPanel.hide(mActionPanel.width / 2, mActionPanel.height / 2)
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    fun doDelete(thing: Thing) {
        "doDelete".logd(TAG)
        ThingsManager.remove(thing)
    }

    fun doDone(thing: Thing) {
        "doDone".logd(TAG)
        ThingsManager.makeComplete(thing, !thing.isComplete)
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val cardBackgroundColor: Int by lazy {
            val a = this@MainActivity.obtainStyledAttributes(null, android.support.v7.cardview.R.styleable.CardView, 0,
                    android.support.v7.cardview.R.style.CardView_Light)
            val backgroundColor = a.getColor(android.support.v7.cardview.R.styleable.CardView_cardBackgroundColor, 0)
            a.recycle()
            backgroundColor
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (v == dotView) {
                // undo complete
                doDone(thing!!)
            } else if (v == itemView) {
                Sound.play(Sound.S_CLICK_ITEM)
                mFabButton.visibility = View.GONE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    var intent = Intent(this@MainActivity, DetailActivity::class.java)
                    val pairDelete = Pair.create(mFabButton as View, "tnDelete")
                    val pairTitle = Pair.create(txtThing as View, "tnTitle")
                    val b = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, pairDelete, pairTitle).toBundle()
                    intent.putExtra(DetailActivity.EXTRA_THING_ID, thing!!.id)
                    startActivityForResult(intent, REQ_DETAIL, b)

                } else {
                    val cx = (itemView.left + itemView.right) / 2
                    val cy = itemView.top + itemView.height
                    mActionPanel.show(cx, cy, thing!!)
                }
            } else if (v == txtDone) {
                val thing = v.tag as Thing
                doDone(thing)
            }
        }

        var thing: Thing? = null

        val dotView: DotView by lazy {
            val v = itemView.findViewById(R.id.dot_view) as DotView
            v.setOnClickListener(this)
            v.doneDrawable = GoogleMaterial.Icon.gmd_done.colorResOf(R.color.redPrimary)
            v
        }

        val txtThing: TextView by lazy {
            val text = itemView.findViewById(R.id.txt_thing) as TextView
            text.typeface = fontSourceSansPro
            text
        }

        val txtReminder: TextView by lazy {
            val text = itemView.findViewById(R.id.txt_reminder) as TextView
            text.typeface = fontSourceSansPro
            text
        }

        val txtDone: TextView by lazy {
            val text = itemView.findViewById(R.id.btn_done) as TextView
            text.setOnClickListener(this)
            text.typeface = fontSourceSansPro
            text
        }

        fun bind(thing: Thing, prevThing: Thing?) {
            this.thing = thing
            updateItemUI(thing, prevThing)
        }

        fun updateItemUI(thing: Thing, prev: Thing?) {

            if (thing.title.isBlank()) {
                txtThing.text = thing.content
            } else {
                txtThing.text = thing.title
            }

            if (Settings.theme == Theme.Dot) {
                dotView.visibility = View.VISIBLE
                dotView.mode = if (thing.isComplete) DotView.Mode.Solid else DotView.Mode.Hollow
                dotView.color = if (thing.isComplete) resources.getColor(R.color.greyPrimary) else thing.color
                txtThing.setTextColor(if (thing.isComplete) resources.getColor(R.color.greyPrimary) else resources.getColor(R.color.blackPrimary))
                (itemView as CardView).setCardBackgroundColor(cardBackgroundColor)
            } else {
                dotView.visibility = View.GONE
                txtThing.setTextColor(resources.getColor(R.color.whitePrimary))
                (itemView as CardView).setCardBackgroundColor(makeThingColor(prev))

                if (thing.isComplete) {
                    txtThing.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                    txtThing.setTypeface(txtThing.typeface, Typeface.ITALIC)
                } else {
                    txtThing.paint.flags = Paint.ANTI_ALIAS_FLAG
                    txtThing.setTypeface(txtThing.typeface, Typeface.BOLD)
                }
            }


            // update reminder text
            if (thing.reminderTime > 0) {
                txtReminder.visibility = View.VISIBLE
                val date = Date(thing.reminderTime)
                txtReminder.text = formatDateTime(date)
                if (Settings.theme != Theme.Colorful) {
                    if (thing.isComplete) {
                        txtReminder.setTextColor(resources.getColor(R.color.greyPrimary))
                    } else {
                        formatTextViewcolor(txtReminder, date)
                    }
                } else {
                    if (thing.isComplete) {
                        txtReminder.setTextColor(resources.getColor(R.color.greyPrimary))
                    } else {
                        txtReminder.setTextColor(resources.getColor(R.color.whitePrimary))
                    }
                }
            } else {
                txtReminder.visibility = View.GONE
            }

            txtDone.tag = thing
            txtDone.visibility = if (Settings.theme == Theme.Dot) View.GONE else View.VISIBLE
            val status =  if (thing.isComplete) R.string.doing.toResString(this@MainActivity) else R.string.done
                    .toResString(this@MainActivity)
            txtDone.text = status
        }

        fun makeThingColor(prevThing: Thing?): Int {
            var thisColor = thing!!.color

            if (prevThing?.color == thisColor) {
                var baseColor = thisColor
                if (prevThing?.displayColor != 0) {
                    baseColor = prevThing!!.displayColor
                }
                val newColor = ColorPicker.getDimedColor(baseColor)
                thing?.displayColor = newColor
                return newColor
            }

            return thisColor
        }
    }

    inner class ThingsAdapter : RecyclerView.Adapter<VH>() {

        var things: List<Thing>? = null
            set(value: List<Thing>?) {
                field = value
                notifyDataSetChanged()
            }

        override fun getItemCount(): Int {
            return things?.size() ?: 0
        }

        override fun onBindViewHolder(holder: VH?, position: Int) {
            val prev = if (position > 0 && position < itemCount) things?.get(position - 1) else null
            holder?.bind(things!![position], prev)
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH? {
            val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.thing_list_item, parent, false)
            return VH(view)
        }

    }
}
