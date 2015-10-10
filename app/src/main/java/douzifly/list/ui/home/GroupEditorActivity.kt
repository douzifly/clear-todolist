package douzifly.list.ui.home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.daimajia.swipe.SwipeLayout
import douzifly.list.R
import douzifly.list.model.ThingGroup
import douzifly.list.model.ThingsManager
import douzifly.list.utils.fontRailway
import douzifly.list.utils.fontSourceSansPro

/**
 * Created by air on 15/10/9.
 */
class GroupEditorActivity : AppCompatActivity() {

  val mRecyclerView : RecyclerView by lazy {
    findViewById(R.id.recycler_view) as RecyclerView
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.home_group_editor_activity)

    mRecyclerView.layoutManager = LinearLayoutManager(this)
    mRecyclerView.adapter = GroupAdapter()

    (mRecyclerView.adapter as GroupAdapter).groups = ThingsManager.groups

    (findViewById(R.id.txt_title) as TextView).typeface = fontRailway

  }

  inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    override fun onClick(p0: View?) {
      val success = ThingsManager.removeGroup(ThingsManager.groups[position].id)
      if (success) {
        mRecyclerView.adapter.notifyDataSetChanged()
      } else {
        Toast.makeText(this@GroupEditorActivity, "Can't delete the last one.", Toast.LENGTH_SHORT).show()
        swipeLayout.close()
      }
    }

    val swipeLayout: SwipeLayout by lazy {
      itemView.findViewById(R.id.swipe_layout) as SwipeLayout
    }

    val actionDelete: View by lazy {
      itemView.findViewById(R.id.action_delete)
    }

    init {
      itemView.findViewById(R.id.content_panel).setOnClickListener {
        ThingsManager.changeGroup(ThingsManager.groups[position].id)
        this@GroupEditorActivity.finish()
      }
      swipeLayout.showMode = SwipeLayout.ShowMode.PullOut
      actionDelete.setOnClickListener(this)
    }

    val mTxtTitle : TextView by lazy {
      val text = itemView.findViewById(R.id.txt_title) as TextView
      text.typeface = fontSourceSansPro
      text
    }
  }

  inner class GroupAdapter : RecyclerView.Adapter<GroupViewHolder>() {

    var groups: List<ThingGroup>? = null
      set(value: List<ThingGroup>?) {
        $groups = value
        notifyDataSetChanged()
      }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GroupViewHolder? {
      return GroupViewHolder(LayoutInflater.from(this@GroupEditorActivity).inflate(R.layout.home_group_item, parent, false))
    }

    override fun onBindViewHolder(holder: GroupViewHolder?, position: Int) {
      holder?.mTxtTitle?.text = groups?.get(position)?.title ?: ""
    }

    override fun getItemCount(): Int {
      return groups?.size() ?: 0
    }

  }
}