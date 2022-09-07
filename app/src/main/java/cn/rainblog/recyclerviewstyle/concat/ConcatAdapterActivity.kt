package cn.rainblog.recyclerviewstyle.concat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.rainblog.recyclerviewstyle.R

class ConcatAdapterActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_concat_adapter)

    val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
    recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

    recyclerView.adapter = ConcatAdapter(
      TwoAdapter(),
      FourAdapter(),
      ThreeAdapter(),
      OneAdapter(),
    )

  }

}