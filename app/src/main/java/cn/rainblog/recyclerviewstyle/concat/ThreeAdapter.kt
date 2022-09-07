package cn.rainblog.recyclerviewstyle.concat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cn.rainblog.recyclerviewstyle.R

class ThreeAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = Type3ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

  override fun getItemCount(): Int = 30

  override fun onViewAttachedToWindow(holder: ViewHolder) {
    super.onViewAttachedToWindow(holder)
    val lp = holder.itemView.layoutParams
    val width = holder.itemView.context.resources.displayMetrics.widthPixels / 2
    lp.width = width
    lp.height = width / 3 * 4
    holder.itemView.layoutParams = lp
  }

  inner class Type3ViewHolder(view: View): RecyclerView.ViewHolder(view) {}
}