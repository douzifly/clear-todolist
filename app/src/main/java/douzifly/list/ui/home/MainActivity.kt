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
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Pair
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.paulburke.android.itemtouchhelperdemo.helper.ItemTouchHelperAdapter
import co.paulburke.android.itemtouchhelperdemo.helper.ItemTouchHelperViewHolder
import co.paulburke.android.itemtouchhelperdemo.helper.OnStartDragListener
import co.paulburke.android.itemtouchhelperdemo.helper.SimpleItemTouchHelperCallback
import com.daimajia.swipe.SwipeLayout
import com.github.clans.fab.FloatingActionButton
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.nineoldandroids.animation.Animator
import com.nineoldandroids.animation.ObjectAnimator
import douzifly.list.R
import douzifly.list.model.Thing
import douzifly.list.model.ThingGroup
import douzifly.list.model.ThingsManager
import douzifly.list.model.randomEmptyText
import douzifly.list.notification.ClearNotification
import douzifly.list.settings.Settings
import douzifly.list.settings.Theme
import douzifly.list.sounds.Sound
import douzifly.list.utils.*
import douzifly.list.widget.*
import java.util.*

class MainActivity : AppCompatActivity(), OnStartDragListener {

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        mItemTouchHelper?.startDrag(viewHolder)
    }

    companion object {
        val TAG = "MainActivity"
        val REQ_SETTING = 1
        val REQ_DETAIL = 2
        val REQ_CHANGE_GROUP = 3
        val REQ_INPUT_PANEL_GROUP = 4
        val REQ_INPUT_SELECT_CHOOSE_GROUP = 5;
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

    val mFabSetting: FloatingActionButton by lazy {
        findViewById(R.id.fab_setting) as FloatingActionButton
    }

    fun refreshList() {
        ui {
            "refreshList".logd(TAG)
            checkShowEmptyText()
            updateTitle()
            if (Settings.selectedGroupId == ThingGroup.SHOW_ALL_GROUP_ID) {
                mTitleLayout.count = ThingsManager.allThingsInComplete
            } else {
                mTitleLayout.count = ThingsManager.getGroupByGroupId(Settings.selectedGroupId)?.inCompleteThingsCount ?: 0
            }
            (mRecyclerView.adapter as ThingsAdapter).things = ThingsManager.getThingsByGroupId(Settings.selectedGroupId)
        }
    }

    var mFontSize = Settings.fontSize

    val mFabListener: (v: View) -> Unit = {

        if (mInputPanel.visibility == View.VISIBLE) {

            val cx = (mFabButton.left + mFabButton.right) / 2
            val cy = mFabButton.top + mFabButton.height / 2
            startCircularReveal(cx, cy, mInputPanel.revealView, true, 200) {
                mInputPanel.visibility = View.INVISIBLE
                setFabAsCommit(false)
                ui {
                    mInputPanel.editText.hideKeyboard()
                    ui(100) {
                        handleInputFinished()
                        mInputPanel.reset()
                        Sound.play(Sound.S_CLICK_DONE)
                    }
                }
            }

        } else {
            val cx = (mFabButton.left + mFabButton.right) / 2
            val cy = mFabButton.top + mFabButton.height / 2
            mInputPanel.visibility = View.VISIBLE
            startCircularReveal(cx, cy, mInputPanel.revealView, false, 200) {
                mInputPanel.editText.requestFocus()
                mInputPanel.editText.showKeyboard()
            }
            setFabAsCommit(true)

            Sound.play(Sound.S_CLICK_ADD)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mFontSize != Settings.fontSize) {
            mFontSize = Settings.fontSize
            mRecyclerView.adapter.notifyDataSetChanged()
            mInputPanel.updateFontSize()
        }
    }

    fun checkShowEmptyText() {
        val things = ThingsManager.getThingsByGroupId(Settings.selectedGroupId)
        mTxtEmpty.visibility = if (things.size == 0) View.VISIBLE else View.GONE
        if (mTxtEmpty.visibility == View.VISIBLE) {
            (mTxtEmpty as TextView).text = randomEmptyText()
        }
    }

    fun handleInputFinished() {
        val textString = mInputPanel.editText.text.toString().trim()
        val contentString = mInputPanel.contentEditText.text.toString()
        if (textString.isBlank() && contentString.isBlank()) {
            return
        }

        val selectedGroup = mInputPanel.selectedGroup

        bg {
            ThingsManager.addThing(selectedGroup!!, textString, contentString
                    , mInputPanel.reminderDate?.time ?: -1, mInputPanel.colorPicker.selectedColor)

            ui {
                if (Settings.selectedGroupId != ThingGroup.SHOW_ALL_GROUP_ID && selectedGroup.id != Settings.selectedGroupId) {
                    // user choose another group and home list group wont show this new item, switch it
                    Settings.selectedGroupId = selectedGroup.id
                }
                refreshList()
            }
        }
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

    var mItemTouchHelper: ItemTouchHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFabButton.setOnClickListener(mFabListener)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ThingsAdapter(this)
        mRecyclerView.adapter = adapter

        // drag and drop
        val callback = SimpleItemTouchHelperCallback(adapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper!!.attachToRecyclerView(mRecyclerView)
        //

        (mTxtEmpty as TextView).typeface = fontAlegreya

        setFabAsCommit(false)
        checkShowEmptyText()

        mTitleLayout.titleClickListener = {
            // click title, show box
            startActivityForResult(Intent(this, GroupEditorActivity::class.java), REQ_CHANGE_GROUP)
        }

        mFabSetting.setOnClickListener {
            var bundle: Bundle? = null

            try {
                bundle = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, mFabSetting, "st").toBundle()
            } catch(e: Exception) {
                // ignore
                // device not support activity transition animation
            } catch(e: Error) {
                // ignore
            }

            startActivityForResult(Intent(this, SettingActivity::class.java), REQ_SETTING, bundle)
        }

        bg {
            ThingsManager.loadFromDb()
            ui {
                refreshList()
                handleIntent(intent)
            }
        }
        Sound.load(this)
    }

    private fun updateTitle() {
        val group = ThingsManager.getGroupByGroupId(Settings.selectedGroupId)
        if (Settings.selectedGroupId == ThingGroup.SHOW_ALL_GROUP_ID) {
            mTitleLayout.title = R.string.default_list.toResString(this@MainActivity)
        } else if (group!!.isDefault) {
            mTitleLayout.title = getString(R.string.default_group_title)
        } else {
            mTitleLayout.title = group.title
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQ_INPUT_PANEL_GROUP == requestCode) {
            // input panel choose group
            if (resultCode == RESULT_OK) {
                val id = data?.getLongExtra("id", -1) ?: -1
                mInputPanel.updateGroupText(ThingsManager.getGroupByGroupId(id)!!)
            }
        } else if (REQ_CHANGE_GROUP == requestCode && resultCode == RESULT_OK) {
            // change group
            val id = data!!.getLongExtra("id", -1)
            Settings.selectedGroupId = id
            refreshList()
        } else if (REQ_SETTING == requestCode) {
            if (resultCode == SettingActivity.RESULT_THEME_CHANGED) {
                mRecyclerView.adapter.notifyDataSetChanged()
            } else if (resultCode == SettingActivity.RESULT_GROUP_CHANGED) {
                refreshList()
            }
        } else if (REQ_DETAIL == requestCode) {
            if (resultCode == DetailActivity.RESULT_DELETE) {
                val id = data?.extras?.getLong(DetailActivity.EXTRA_THING_ID) ?: -1L
                var thing: Thing? = null
                if (id > 0) {
                    thing = ThingsManager.getThingByIdAtCurrentGroup(id)
                }
                if (thing == null) return
                doDelete(thing)
            } else if (resultCode == DetailActivity.RESULT_UPDATE) {
                refreshList()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ThingsManager.release()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        "onNewIntent: $intent".logd(TAG)
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }
        val thingId = intent.getIntExtra(ClearNotification.EXTRA_THING_ID, -1)
        "handle intent thingId: $thingId".logd(TAG)
        if (thingId >= 0) {
            val thing = ThingsManager.getThingById(thingId.toLong())
            "handle intent thing: $thing".logd(TAG)
            if (thing != null) {
                Settings.selectedGroupId = thing.group!!.id
                refreshList()
            }
        }

    }

    fun startCircularReveal(cx: Int, cy: Int, viewRoot: View, reverse: Boolean, duration: Int, end: (() -> Unit)? = null) {
        val endRadius = Math.max(viewRoot.width, viewRoot.height).toFloat()
        val startRadius = if (reverse) endRadius else 0f
        val finalRadius = if (reverse) 0f else endRadius
        val anim = io.codetail.animation.ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, startRadius, finalRadius)
        anim.setDuration(duration)
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
        }
        return super.onKeyDown(keyCode, event)
    }

    fun doDelete(thing: Thing) {
        "doDelete".logd(TAG)
        bg {
            ThingsManager.remove(thing)
            Sound.play(Sound.S_DELETE)
            ui {
                refreshList()
            }
        }
    }

    fun doDone(thing: Thing) {
        "doDone".logd(TAG)
        bg {
            ThingsManager.makeComplete(thing, !thing.isComplete)
            Sound.play(Sound.S_DONE)
            ui {
                refreshList()
            }
        }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener, ItemTouchHelperViewHolder {

        override fun onItemSelected() {
            itemView.scaleX = 1.02f
            itemView.scaleY = 1.1f
        }

        override fun onItemClear() {
            itemView.scaleX = 1f
            itemView.scaleY = 1f
        }

        val cardBackgroundColor: Int by lazy {
            val a = this@MainActivity.obtainStyledAttributes(null, android.support.v7.cardview.R.styleable.CardView, 0,
                    android.support.v7.cardview.R.style.CardView_Light)
            val backgroundColor = a.getColor(android.support.v7.cardview.R.styleable.CardView_cardBackgroundColor, 0)
            a.recycle()
            backgroundColor
        }

        init {
            itemView.findViewById(R.id.content).setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (v == dotView) {
                // undo complete
                doDone(thing!!)
            } else if (v!!.id == R.id.content) {
                Sound.play(Sound.S_CLICK_ITEM)

                if (swipeLayout.openStatus == SwipeLayout.Status.Open) {
                    swipeLayout.close(true)
                    return
                }

                var intent = Intent(this@MainActivity, DetailActivity::class.java)
                var b: Bundle? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val pairDelete = Pair.create(mFabButton as View, "tnDelete")
                    val pairTitle = Pair.create(txtThing as View, "tnTitle")
                    b = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, pairDelete, pairTitle).toBundle()
                }
                intent.putExtra(DetailActivity.EXTRA_THING_ID, thing!!.id)
                startActivityForResult(intent, REQ_DETAIL, b)
            } else if (v == txtDone) {
                val thing = v.tag as Thing
                swipeLayout.close(true)
                ui(500) {
                    var delay: Long = 100
                    if (!thing.isComplete) {
                        delay = 1000
                        showThumbUp()
                    }
                    ui (delay) {
                        doDone(thing)
                    }
                }
            } else if (v == txtDelete) {
                val thing = v.tag as Thing
                swipeLayout.close(true)
                ui(500) {
                    doDelete(thing)
                }
            }
        }

        fun showThumbUp() {
            thumbUp.visibility = View.VISIBLE
            val cx = (thumbUp.left + thumbUp.right) / 2
            val cy = thumbUp.top + thumbUp.height / 2
            val alpha = ObjectAnimator.ofFloat(thumbUp, "alpha", 0.0f, 1.0f)
            alpha.duration = 200
            alpha.start()
            startCircularReveal(cx, cy, thumbUp, false, 600) {
                hideThumbUp()
            }
        }

        fun hideThumbUp() {
            val alpha = ObjectAnimator.ofFloat(thumbUp, "alpha", 1.0f, 0.0f)
            alpha.duration = 300
            alpha.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                    thumbUp.visibility = View.INVISIBLE
                }

                override fun onAnimationEnd(animation: Animator?) {
                    thumbUp.visibility = View.INVISIBLE
                }

                override fun onAnimationStart(animation: Animator?) {
                }

            })
            alpha.start()

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

        val txtDelete: TextView by lazy {
            val text = itemView.findViewById(R.id.btn_delete) as TextView
            text.setOnClickListener(this)
            text.typeface = fontSourceSansPro
            text
        }

        val swipeLayout: SwipeLayout by lazy {
            itemView.findViewById(R.id.swipe_layout) as SwipeLayout
        }

        val thumbUp: View by lazy {
            itemView.findViewById(R.id.thumb_up)
        }

        val txtTimeDiff: TextView by lazy {
            val text = itemView.findViewById(R.id.txt_duration) as TextView
            text.typeface = fontSourceSansPro
            text
        }

        val txtGroup: TextView by lazy {
            val text = itemView.findViewById(R.id.txt_group) as TextView
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
                txtGroup.setTextColor(if (thing.isComplete) resources.getColor(R.color.greyPrimary) else resources.getColor(R.color.blackPrimary))
                txtTimeDiff.setTextColor(if (thing.isComplete) resources.getColor(R.color.greyPrimary) else resources.getColor(R.color.blackPrimary))
                (itemView as CardView).setCardBackgroundColor(cardBackgroundColor)
                if (thing.isComplete) {
                    txtThing.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                    txtThing.setTypeface(txtThing.typeface, Typeface.ITALIC)
                } else {
                    txtThing.paint.flags = Paint.ANTI_ALIAS_FLAG
                    txtThing.setTypeface(txtThing.typeface, Typeface.NORMAL)
                }
            } else {
                dotView.visibility = View.GONE
                txtThing.setTextColor(resources.getColor(R.color.whitePrimary))
                txtGroup.setTextColor(resources.getColor(R.color.whitePrimary))
                txtTimeDiff.setTextColor(resources.getColor(R.color.whitePrimary))
                (itemView as CardView).setCardBackgroundColor(makeThingColor(prev))

                if (thing.isComplete) {
                    txtThing.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                    txtThing.setTypeface(txtThing.typeface, Typeface.ITALIC)
                } else {
                    txtThing.paint.flags = Paint.ANTI_ALIAS_FLAG
                    txtThing.setTypeface(txtThing.typeface, Typeface.NORMAL)
                }
            }

            // font size
            val fontSize = FontSizeBar.fontSizeToDp(mFontSize)
            txtThing.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
            txtGroup.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize - 6)

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

            // done text
            txtDone.tag = thing

            txtDone.text =
                    if (thing.isComplete) R.string.doing.toResString(this@MainActivity)
                    else R.string.done.toResString(this@MainActivity)

            txtDone.setBackgroundColor(
                    if (thing.isComplete) resources.getColor(R.color.greyPrimary)
                    else resources.getColor(R.color.greenPrimary)
            )

            txtDelete.tag = thing
            "update txtDone".logd(TAG)

            // time text
            txtTimeDiff.text = "${Date(thing.creationTime).formatDaysAgoFromNow(this@MainActivity)}"
            txtTimeDiff.visibility = if (txtTimeDiff.text == "") View.GONE else View.VISIBLE

            // txtGroup
            if (Settings.selectedGroupId == ThingGroup.SHOW_ALL_GROUP_ID) {
                // show group text
                txtGroup.visibility = View.VISIBLE
                txtGroup.text = "#${thing.group!!.title}"
            } else {
                txtGroup.visibility = View.GONE
            }
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

    inner class ThingsAdapter(val dragListener: OnStartDragListener) : RecyclerView.Adapter<VH>(), ItemTouchHelperAdapter {

        override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
            if (Settings.selectedGroupId == ThingGroup.SHOW_ALL_GROUP_ID) return false
            val success= ThingsManager.swapThings(ThingsManager.getGroupByGroupId(Settings.selectedGroupId)!!, fromPosition, toPosition)
            if (!success) {
                return false
            }
            notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onItemDismiss(position: Int) {
        }

        var things: List<Thing>? = null
            set(value: List<Thing>?) {
                field = value
                notifyDataSetChanged()
            }

        override fun getItemCount(): Int {
            return things?.size ?: 0
        }

        override fun onBindViewHolder(holder: VH?, position: Int) {
            val prev = if (position > 0 && position < itemCount) things?.get(position - 1) else null
            holder?.bind(things!![position], prev)
            holder?.itemView?.setOnLongClickListener {
                v ->
                dragListener.onStartDrag(holder)
                false
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH? {
            val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.thing_list_item, parent, false)
            return VH(view)
        }

    }
}
