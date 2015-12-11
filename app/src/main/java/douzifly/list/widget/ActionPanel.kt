package douzifly.list.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.github.clans.fab.FloatingActionButton
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.nineoldandroids.animation.AnimatorSet
import com.nineoldandroids.animation.ObjectAnimator
import douzifly.list.R
import douzifly.list.model.Thing
import douzifly.list.utils.colorOf
import douzifly.list.utils.colorResOf
import douzifly.list.utils.fontSourceSansPro
import io.codetail.widget.RevealFrameLayout

/**
 * Created by air on 15/10/17.
 */
class ActionPanel(context: Context, attrs: AttributeSet) : RevealFrameLayout(context, attrs) {

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

  val txtContent: TextView by lazy {
    findViewById(R.id.txt_content) as TextView
  }

  val revealView: View by lazy {
    getChildAt(0)
  }

  var onDoneListener: ((Thing)-> Unit)? = null
  var onDeleteListener: ((Thing)-> Unit)? = null
  var onHide:(()->Unit)? = null

  var thing: Thing? = null

  var isShowing: Boolean = false
    private set

  fun show(cx: Int, cy: Int, thing: Thing) {
    txtTitle.visibility = View.INVISIBLE
    txtContent.visibility = View.INVISIBLE
    actionDelete.visibility = View.INVISIBLE
    actionDone.visibility = View.INVISIBLE

    txtTitle.text = thing.title
    txtContent.text = thing.content
    isShowing = true
    this.thing = thing
    visibility = View.VISIBLE
    startCircularReveal(cx, cy, revealView, false) {
      // show button animation
      actionDone.visibility = View.VISIBLE
      actionDelete.visibility = View.VISIBLE
      txtTitle.visibility = View.VISIBLE
      txtContent.visibility = View.VISIBLE

      val alphaIn0 = ObjectAnimator.ofFloat(actionDelete, "alpha", 0.0f, 1.0f)
      val alphaIn1 = ObjectAnimator.ofFloat(actionDone, "alpha", 0.0f, 1.0f)
      val alphaIn2 = ObjectAnimator.ofFloat(txtTitle, "alpha", 0.1f, 1.0f)

      val set = AnimatorSet()
      set.playTogether(alphaIn0, alphaIn1, alphaIn2)
      set.start()
    }
  }

  fun hide(cx: Int, cy: Int, cb:(()->Unit)? = null) {
    isShowing = false
    startCircularReveal(cx, cy, revealView, true) {
      visibility = View.INVISIBLE
      cb?.invoke()
      onHide?.invoke()
    }
  }

  fun startCircularReveal(cx: Int, cy: Int, viewRoot: View, reverse: Boolean, end: (() -> Unit)? = null) {
    val endRadius = Math.max(viewRoot.width, viewRoot.height).toFloat()
    val startRadius = if (reverse) endRadius else 0f
    val finalRadius = if (reverse) 0f else endRadius
    val anim = io.codetail.animation.ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, startRadius, finalRadius)
    anim.setDuration(400)
    anim.addListener(object : io.codetail.animation.SupportAnimator.AnimatorListener {
      override fun onAnimationRepeat() {
      }

      override fun onAnimationCancel() {
      }

      override fun onAnimationEnd() {
        end?.invoke()
        //        anim.removeListener(this)
      }

      override fun onAnimationStart() {
      }

    })
    anim.start()
  }

  override fun onFinishInflate() {
    super.onFinishInflate()

    txtTitle.typeface = fontSourceSansPro
    txtContent.typeface = fontSourceSansPro

    setOnClickListener {
      // no ops, eat event
      hide(width / 2, height/ 2)
    }

    actionDelete.setOnClickListener {

      var locations = IntArray(2)
      actionDelete.getLocationInWindow(locations)


      hide(locations[0] + actionDelete.width / 2, locations[1] + actionDelete.height / 2) {
        onDeleteListener?.invoke(thing!!)
      }

    }

    actionDone.setOnClickListener {
      var locations = IntArray(2)
      actionDone.getLocationInWindow(locations)
      hide(locations[0] + actionDone.width / 2, locations[1] + actionDone.height / 2) {
        onDoneListener?.invoke(thing!!)
      }
    }

  }
}
