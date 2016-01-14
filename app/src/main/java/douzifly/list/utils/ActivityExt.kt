package douzifly.list.utils

import android.app.Activity
import android.app.ProgressDialog

/**
 * Created by douzifly on 1/14/16.
 */

fun Activity.showProgressDialog(title:String): ProgressDialog {
    val pd = ProgressDialog(this)
    pd.setTitle(title)
    pd.show()
    return pd
}

fun Activity.showProgressDialogAndHide(title:String, hideMills: Long){
    val pd = ProgressDialog(this)
    pd.setTitle(title)
    pd.show()
    ui (hideMills){
        pd.dismiss()
    }
}