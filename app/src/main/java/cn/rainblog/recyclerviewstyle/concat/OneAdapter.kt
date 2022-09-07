package cn.rainblog.recyclerviewstyle.concat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.rainblog.recyclerviewstyle.R

class OneAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  inner class LoadMoreViewHolder(view: View): RecyclerView.ViewHolder(view) {}

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = LoadMoreViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_load_more, parent, false))

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

  override fun onViewAttachedToWindow(holder: ViewHolder) {
    super.onViewAttachedToWindow(holder)

    (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
  }

  override fun getItemCount(): Int = 1
}