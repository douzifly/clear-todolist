package douzifly.list.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by douzifly on 12/16/15.
 */
inline fun View.showKeyboard() =
    (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
    .showSoftInput(this, InputMethodManager.SHOW_FORCED)

inline fun View.hideKeyboard() =
    (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(this.windowToken, 0)
