package douzifly.list.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.github.clans.fab.FloatingActionButton
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.nineoldandroids.animation.ObjectAnimator
import douzifly.list.R
import douzifly.list.model.Thing
import douzifly.list.model.ThingsManager
import douzifly.list.utils.colorOf
import douzifly.list.utils.colorResOf
import douzifly.list.utils.fontSourceSansPro

/**
 * Created by douzifly on 12/14/15.
 */
class DetailActivity : AppCompatActivity() {


    companion object {
        public val EXTRA_THING_ID = "thing_id"
        private val TAG = "DetailActivity"
        public val RESULT_DONE = 1
        public val RESULT_DELETE = 2
    }

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

    val txtContent: TextView by lazy {
        findViewById(R.id.txt_content) as TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)
        initView()
        parseIntent()
        loadData()

        val alphaAnim = ObjectAnimator.ofFloat(txtContent, "alpha", 0.0f, 1.0f)
        alphaAnim.setDuration(500)
        alphaAnim.start()
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
        txtContent.text = thing!!.content
    }

    fun initView() {

        txtTitle.typeface = fontSourceSansPro
        txtContent.typeface = fontSourceSansPro


        actionDelete.setOnClickListener {
            val intent = Intent()
            intent.putExtra(EXTRA_THING_ID, thing!!.id)
            setResult(RESULT_DELETE, intent)
            finishAfterTransition()
        }

        actionDone.setOnClickListener {
            val intent = Intent()
            intent.putExtra(EXTRA_THING_ID, thing!!.id)
            setResult(RESULT_DONE, intent)
            finishAfterTransition()
        }

    }

}