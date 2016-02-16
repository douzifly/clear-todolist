package douzifly.list.ui.home

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.daimajia.swipe.SwipeLayout
import com.github.clans.fab.FloatingActionButton
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import douzifly.list.R
import douzifly.list.model.ThingGroup
import douzifly.list.model.ThingsManager
import douzifly.list.settings.Settings
import douzifly.list.utils.*

/**
 * Created by air on 15/10/9.
 */
class GroupEditorActivity : AppCompatActivity() {

    companion object {
        val EXTRA_KEY_SHOW_ALL = "show_all"
    }

    val mRecyclerView: RecyclerView by lazy {
        findViewById(R.id.recycler_view) as RecyclerView
    }

    val mFabAdd: FloatingActionButton by lazy {
        findViewById(R.id.fab_add) as FloatingActionButton
    }

    var mAdapter: GroupAdapter = GroupAdapter()
    var mAddEditText: EditText? = null
    var addShowAllItem = true

    fun handleIntent(intent: Intent?) {
        if (intent == null) return
        addShowAllItem = intent.getBooleanExtra(EXTRA_KEY_SHOW_ALL, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)

        setContentView(R.layout.home_group_editor_activity)

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter
        mAdapter.groups = ThingsManager.groups

        (findViewById(R.id.txt_title) as TextView).typeface = fontRailway

        mFabAdd.setOnClickListener {
            if (mAdapter.showAddEditorBox) {
                // click to commit
                commitNewItem()
            } else {
                mAdapter.showAddEditorBox = true
                mRecyclerView.scrollToPosition(mAdapter.itemCount - 1)
                // show button as commit
                mFabAdd.setImageDrawable(
                        GoogleMaterial.Icon.gmd_done.colorResOf(R.color.redPrimary)
                )
            }
        }

        mFabAdd.setImageDrawable(GoogleMaterial.Icon.gmd_add.colorResOf(R.color.greyPrimary))
    }

    fun commitNewItem() {
        mAddEditText?.hideKeyboard()
        val text = mAddEditText!!.text.toString()
        if (text.isNotBlank()) {
            ThingsManager.addGroup(text)
            mAddEditText!!.setText("")
        }
        mAdapter.showAddEditorBox = false
        // show button as add
        mFabAdd.setImageDrawable(
                GoogleMaterial.Icon.gmd_add.colorResOf(R.color.greyPrimary)
        )
    }

    inner class EditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editText: EditText by lazy {
            itemView.findViewById(R.id.edit_text) as EditText
        }

        init {
            editText.typeface = fontSourceSansPro
            editText.setOnEditorActionListener { textView, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    commitNewItem()
                    true
                }
                false
            }
        }
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(p0: View?) {

            val id = itemView.findViewById(R.id.content_panel).tag as Long
            val success = ThingsManager.removeGroup(id)
            if (success) {
                mRecyclerView.adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this@GroupEditorActivity, R.string.cant_remove, Toast.LENGTH_SHORT).show()
                swipeLayout.close()
            }
        }

        val swipeLayout: SwipeLayout by lazy {
            itemView.findViewById(R.id.swipe_layout) as SwipeLayout
        }

        val actionDelete: FloatingActionButton by lazy {
            itemView.findViewById(R.id.action_delete) as FloatingActionButton
        }

        init {
            itemView.findViewById(R.id.content_panel).setOnClickListener {
                v->
                val id = v.tag as Long
                val intent = Intent()
                intent.putExtra("id", id)
                setResult(RESULT_OK, intent)
                this@GroupEditorActivity.finish()
            }
            swipeLayout.showMode = SwipeLayout.ShowMode.PullOut
            actionDelete.setOnClickListener(this)

            actionDelete.setImageDrawable(
                    GoogleMaterial.Icon.gmd_delete.colorResOf(R.color.redPrimary)
            )
        }

        val mTxtTitle: TextView by lazy {
            val text = itemView.findViewById(R.id.txt_title) as TextView
            text.typeface = fontSourceSansPro
            text
        }

        val mTxtCount: TextView by lazy {
            val text = itemView.findViewById(R.id.txt_count) as TextView
            text.typeface = fontSourceSansPro
            text
        }
    }

    inner class GroupAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var showAddEditorBox = false
            set(value: Boolean) {
                field = value
                notifyDataSetChanged()
            }

        override fun getItemViewType(position: Int): Int {
            if (showAddEditorBox && (position == itemCount - 1)) {
                return 1 // add edittext
            }
            return 0
        }

        var groups: List<ThingGroup>? = null
            set(value: List<ThingGroup>?) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
            val lf = LayoutInflater.from(this@GroupEditorActivity)
            return if (viewType == 0) GroupViewHolder(lf.inflate(R.layout.home_group_item, parent, false))
            else EditViewHolder(lf.inflate(R.layout.home_group_ed_item, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            if (addShowAllItem && position == 0) {
                // all group item
                if (holder is GroupViewHolder) {
                    holder.mTxtTitle.text = R.string.group_all.toResString(this@GroupEditorActivity)
                    holder.mTxtCount.text = "${ThingsManager.allThingsInComplete}"
                    holder.itemView.findViewById(R.id.content_panel).tag = ThingGroup.SHOW_ALL_GROUP_ID
                    holder.itemView.background = if (Settings.selectedGroupId == ThingGroup.SHOW_ALL_GROUP_ID)
                                                    resources.getDrawable(R.color.material_grey_50)
                                                    else resources.getDrawable(R.color.transparent)
                    holder.itemView.findViewById(R.id.content_panel).tag = ThingGroup.SHOW_ALL_GROUP_ID
                }

                return
            }

            var realPos = position
            if (addShowAllItem) {
                realPos = position - 1
            }

            if (holder is GroupViewHolder) {
                val group = groups!!.get(realPos)
                holder.mTxtTitle.text = group.title
                holder.mTxtCount.text = "${group.inCompleteThingsCount}"
                holder.itemView.background = if (group!!.isGroupSelected()) resources.getDrawable(R.color.material_grey_50)
                else resources.getDrawable(R.color.transparent)

                holder.itemView.findViewById(R.id.content_panel).tag = group.id
            } else if (holder is EditViewHolder) {
                holder.editText.requestFocus()
                mAddEditText = holder.editText
                ui(300) {
                    holder.editText.showKeyboard()
                }
            }

        }

        override fun getItemCount(): Int {
            var size = groups?.size ?: 0
            if (showAddEditorBox) size++
            if (addShowAllItem) {
                return size + 1 // position 0 is all group
            } else {
                return size
            }
        }

    }
}