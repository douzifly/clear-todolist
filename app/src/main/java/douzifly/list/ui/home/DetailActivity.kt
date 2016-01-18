package douzifly.list.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.github.clans.fab.FloatingActionButton
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.nineoldandroids.animation.ObjectAnimator
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import douzifly.list.R
import douzifly.list.model.Thing
import douzifly.list.model.ThingsManager
import douzifly.list.utils.*
import douzifly.list.widget.ColorPicker
import java.util.*

/**
 * Created by douzifly on 12/14/15.
 */
class DetailActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        reminderDate = Date(year - 1900, monthOfYear, dayOfMonth)
        showTimePicker()
    }

    override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int) {
        "${hourOfDay} : ${minute}".logd("oooo")
        reminderDate?.hours = hourOfDay
        reminderDate?.minutes = minute
        updateTimeUI(reminderDate)
    }

    fun updateTimeUI(date: Date?) {
        if (date == null) {
            txtReminder.text = ""
        } else {
            txtReminder.text = formatDateTime(date)
            formatTextViewcolor(txtReminder, date)
        }
    }


    companion object {
        public val EXTRA_THING_ID = "thing_id"
        private val TAG = "DetailActivity"
        public val RESULT_DONE = 1
        public val RESULT_DELETE = 2
    }

    var reminderDate: Date? = null
    var thing: Thing? = null

    val actionDone: FloatingActionButton by lazy {
        val v = findViewById(R.id.action_done) as FloatingActionButton
        v.setImageDrawable(
                GoogleMaterial.Icon.gmd_done.colorResOf(R.color.redPrimary)
        )
        v
    }

    val actionDelete: FloatingActionButton by lazy {
        val v = findViewById(R.id.action_delete) as FloatingActionButton
        v.setImageDrawable(
                GoogleMaterial.Icon.gmd_delete.colorOf(Color.WHITE)
        )
        v
    }

    val txtTitle: TextView by lazy {
        findViewById(R.id.txt_title) as TextView
    }

    val editTitle: EditText by lazy {
        findViewById(R.id.edit_title) as EditText
    }

    val editContent: EditText by lazy {
        findViewById(R.id.txt_content) as EditText
    }

    val addReminder: FloatingActionButton by lazy {
        findViewById(R.id.fab_add_reminder) as FloatingActionButton
    }

    val txtReminder: TextView by lazy {
        findViewById(R.id.txt_reminder) as TextView
    }

    val toolbar: Toolbar by lazy {
        findViewById(R.id.tool_bar) as Toolbar
    }

    val colorPicker: ColorPicker by lazy {
        findViewById(R.id.color_picker) as ColorPicker
    }

    val focusChangeListener = View.OnFocusChangeListener {
        v, hasFocus ->
        if (!hasFocus) {
            when (v) {
                editTitle -> {
                    setTitleEditMode(false)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)
        initView()
        parseIntent()

        setSupportActionBar(toolbar)
        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.title = ""
        toolbar.setNavigationOnClickListener {
            finishAfterTransition()
            //            saveData()
        }

        val alphaAnim = ObjectAnimator.ofFloat(editContent, "alpha", 0.0f, 1.0f)
        alphaAnim.setDuration(500)
        alphaAnim.start()


        addReminder.setImageDrawable(
                GoogleMaterial.Icon.gmd_alarm.colorResOf(R.color.greyPrimary)
        )

        addReminder.setOnClickListener {
            showDatePicker()
        }

        txtReminder.typeface = fontRailway

        loadData()
    }

    fun cancelPickTime() {
        reminderDate = null
        txtReminder.text = ""
    }

    fun showDatePicker() {
        val now = Calendar.getInstance();
        val dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.accentColor = colorPicker.selectedColor
        dpd.setOnCancelListener {
            cancelPickTime()
        }

        ui {
            editTitle.hideKeyboard()
            editContent.hideKeyboard()
        }
        dpd.show((this as AppCompatActivity).getFragmentManager(), "Datepickerdialog");
    }

    fun showTimePicker() {
        val now = Calendar.getInstance();
        val dpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true)
        dpd.accentColor = colorPicker.selectedColor
        dpd.setOnCancelListener {
            cancelPickTime()
        }

        dpd.show((this as AppCompatActivity).getFragmentManager(), "Timepickerdialog");
    }


    fun parseIntent() {
        val id = intent?.getLongExtra(EXTRA_THING_ID, 0) ?: 0
        if (id > 0) {
            thing = ThingsManager.currentGroup?.findThing(id)
        }
    }

    fun loadData() {
        if (thing == null) return
        txtTitle.text = thing!!.title
        editTitle.setText(thing!!.title)
        editContent.setText(thing!!.content)
        editContent.setSelection(thing!!.content.length)
        editContent.setBackgroundColor(0x0000)

        ui(200) {
            colorPicker.setSelected(thing!!.color)
        }

        updateTimeUI(
                if (thing!!.reminderTime > 0) Date(thing!!.reminderTime)
                else null
        )
    }

    fun saveData() {

        editTitle.hideKeyboard()
        editContent.hideKeyboard()


        var changed = false
        val newTitle = editTitle.text.toString()
        val newContent = editContent.text.toString()
        val newColor = colorPicker.selectedColor
        if (thing!!.title != newTitle) {
            thing!!.title = newTitle
            changed = true
        }
        if (thing!!.content != newContent) {
            thing!!.content = newContent
            changed = true
        }

        if (thing!!.color != newColor) {
            thing!!.color = newColor
            ThingsManager.currentGroup?.clearAllDisplayColor()
            changed = true
        }

        if (thing!!.reminderTime != reminderDate?.time) {
            thing!!.reminderTime = reminderDate?.time ?: 0
            changed = true
        }

        if (changed) {
            bg {
                ThingsManager.saveThing(thing!!)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //        saveData()
    }

    fun initView() {

        txtTitle.typeface = fontSourceSansPro
        editContent.typeface = fontSourceSansPro
        editTitle.typeface = fontSourceSansPro

        editTitle.visibility = View.GONE


        actionDelete.setOnClickListener {
            val intent = Intent()
            intent.putExtra(EXTRA_THING_ID, thing!!.id)
            setResult(RESULT_DELETE, intent)
            finishAfterTransition()
        }

        actionDone.setOnClickListener {
            //            val intent = Intent()
            //            intent.putExtra(EXTRA_THING_ID, thing!!.id)
            //            setResult(RESULT_DONE, intent)
            finishAfterTransition()
            bg {
                saveData()
            }
        }

        txtTitle.setOnClickListener(onClickListener)

        editTitle.onFocusChangeListener = focusChangeListener
        editContent.isFocusable = false
        editContent.isFocusableInTouchMode = false
        editContent.setOnClickListener {
            v ->
            editContentRequestFocus()
        }

        editTitle.setOnEditorActionListener {
            textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                editContentRequestFocus()
                true
            }
            false
        };
    }

    private fun editContentRequestFocus() {
        editContent.isFocusable = true
        editContent.isFocusableInTouchMode = true
        editContent.requestFocus()
        ui(100) {
            editContent.showKeyboard()
        }
    }

    fun setTitleEditMode(editMode: Boolean) {
        if (editMode) {
            // show edittext
            txtTitle.visibility = View.GONE
            editTitle.setText(txtTitle.text)
            editTitle.visibility = View.VISIBLE
            editTitle.requestFocus()
            editTitle.showKeyboard()
        } else {
            txtTitle.visibility = View.VISIBLE
            editTitle.visibility = View.GONE
            txtTitle.setText(editTitle.text)
        }
    }


    val onClickListener: (v: View) -> Unit = {
        v ->
        if (v == txtTitle) {
            setTitleEditMode(true)
        }
    }

}