package douzifly.list.ui.home

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
import douzifly.list.utils.*

/**
 * Created by air on 15/10/9.
 */
class GroupEditorActivity : AppCompatActivity() {

  val mRecyclerView : RecyclerView by lazy {
    findViewById(R.id.recycler_view) as RecyclerView
  }

  val mFabAdd : FloatingActionButton by lazy {
    findViewById(R.id.fab_add) as FloatingActionButton
  }

  var mAdapter : GroupAdapter = GroupAdapter()
  var mAddEditText: EditText? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

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
      val success = ThingsManager.removeGroup(ThingsManager.groups[position].id)
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
        ThingsManager.changeGroup(ThingsManager.groups[position].id)
        this@GroupEditorActivity.finish()
      }
      swipeLayout.showMode = SwipeLayout.ShowMode.PullOut
      actionDelete.setOnClickListener(this)

      actionDelete.setImageDrawable(
              GoogleMaterial.Icon.gmd_delete.colorResOf(R.color.redPrimary)
      )
    }

    val mTxtTitle : TextView by lazy {
      val text = itemView.findViewById(R.id.txt_title) as TextView
      text.typeface = fontSourceSansPro
      text
    }

    val mTxtCount : TextView by lazy {
      val text = itemView.findViewById(R.id.txt_count) as TextView
      text.typeface = fontSourceSansPro
      text
    }
  }

  inner class GroupAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var showAddEditorBox = false
      set(value: Boolean) {
//        if (value == $showAddEditorBox) return
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
      if (holder is GroupViewHolder) {
        val group = groups!!.get(position)
        holder.mTxtTitle.text = group.title
        holder.mTxtCount.text = "${group.unCompleteThingsCount}"
        holder.itemView.background = if (group!!.selected) resources.getDrawable(R.color.material_grey_50) else resources.getDrawable(R.color.transparent)
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
      return size
    }

  }
}