package douzifly.list.utils

import android.content.Context
import android.text.format.DateUtils
import android.widget.TextView
import douzifly.list.ListApplication
import douzifly.list.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by air on 15/10/18.
 */
fun formatDateTime(date: Date): String {
    if (DateUtils.isToday(date.time)) {
        return "${ListApplication.appContext?.resources?.getString(R.string.today)} ${SimpleDateFormat("HH:mm").format(date)}"
    }
    val datePrevDay = Date(date.time - 24 * 60 * 60 * 1000)
    if (DateUtils.isToday(datePrevDay.time)) {
        return "${ListApplication.appContext?.resources?.getString(R.string.tommorow)} ${SimpleDateFormat("HH:mm").format(date)}"
    }

    return SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
}

fun formatTextViewcolor(tv: TextView, date: Date) {
    if (date.time < Date().time) {
        tv.setTextColor(tv.resources.getColor(R.color.redPrimary))
    } else {
        tv.setTextColor(tv.resources.getColor(R.color.greyPrimary))
    }
}

fun Date.dayDiff(otherDate: Date): Long {
    return (otherDate.getTime() - getTime()) / 86400000
}

fun Date.formatDaysAgoFromNow(context: Context): String {
    val diff = this.dayDiff(Date(System.currentTimeMillis()))
    if (diff <= 0) return ""
    if (diff > 0) return R.string.day_diff.toResString(context).format(diff)
    return ""
}