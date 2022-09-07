package cn.rainblog.recyclerviewstyle.concat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.rainblog.recyclerviewstyle.R

class FourAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = Type1ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

  override fun getItemCount(): Int = 3

  override fun onViewAttachedToWindow(holder: ViewHolder) {
    super.onViewAttachedToWindow(holder)
    (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).apply {
      isFullSpan = true
      width = holder.itemView.context.resources.displayMetrics.widthPixels
      height = width / 4
      holder.itemView.layoutParams = this
    }
  }

  inner class Type1ViewHolder(view: View): RecyclerView.ViewHolder(view) {}
}