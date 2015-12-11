package douzifly.list.widget

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.github.clans.fab.FloatingActionButton
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import douzifly.list.R
import douzifly.list.utils.*
import io.codetail.widget.RevealFrameLayout
import java.util.*

/**
 * Created by douzifly on 15/10/17.
 */
class InputPanel(context: Context, attrs: AttributeSet) : RevealFrameLayout(context, attrs),
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int) {
        "${hourOfDay} : ${minute}".logd("oooo")
        reminderDate?.hours = hourOfDay
        reminderDate?.minutes = minute
        updateTimeUI()
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        reminderDate = Date(year - 1900, monthOfYear, dayOfMonth)
        showTimePicker()
    }

    public var reminderDate: Date? = null

    val editText: EditText by lazy {
        findViewById(R.id.edit_text) as EditText
    }

    val contentEditText: EditText by lazy {
        findViewById(R.id.edit_text_content) as EditText
    }

    val addReminder: FloatingActionButton by lazy {
        findViewById(R.id.fab_add_reminder) as FloatingActionButton
    }

    val colorPicker: ColorPicker by lazy {
        findViewById(R.id.color_picker) as ColorPicker
    }

    val revealView: View by lazy {
        getChildAt(0)
    }

    val txtReminder: TextView by lazy {
        findViewById(R.id.txt_reminder) as TextView
    }

    fun reset() {
        editText.setText("")
        txtReminder.setText("")
        contentEditText.setText("")
        reminderDate = null
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        editText.typeface = fontSourceSansPro
        contentEditText.typeface = fontSourceSansPro

        setOnClickListener {
            // eat event
        }

        addReminder.setImageDrawable(
                GoogleMaterial.Icon.gmd_alarm.colorResOf(R.color.greyPrimary)
        )

        addReminder.setOnClickListener {
            showDatePicker()
        }

        txtReminder.typeface = fontRailway
    }

    fun cancelPickTime() {
        reminderDate = null
        txtReminder.text = ""
    }

    fun updateTimeUI() {
        txtReminder.text = formatDateTime(reminderDate!!)
        formatTextViewcolor(txtReminder, reminderDate!!)
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
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(editText.windowToken, 0)
        }
        dpd.show((context as AppCompatActivity).getFragmentManager(), "Datepickerdialog");
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

        dpd.show((context as AppCompatActivity).getFragmentManager(), "Timepickerdialog");
    }

}