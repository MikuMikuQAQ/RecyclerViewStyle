package cn.rainblog.recyclerviewstyle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MoreViewHolderActivity : AppCompatActivity() {

  private lateinit var mRecyclerView: RecyclerView
  private lateinit var mLayoutManager: StaggeredGridLayoutManager
  private val mAdapter: DemoAdapter by lazy {
    DemoAdapter()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_more_view_holder)

    mRecyclerView = findViewById(R.id.recycler_view)
    mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
      mRecyclerView.layoutManager = this
    }
    mRecyclerView.adapter = mAdapter
  }
}