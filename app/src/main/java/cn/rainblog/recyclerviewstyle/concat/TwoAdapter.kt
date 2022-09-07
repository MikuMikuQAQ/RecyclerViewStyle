package cn.rainblog.recyclerviewstyle.concat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.rainblog.recyclerviewstyle.R

class TwoAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = Type0ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_top_list, parent, false))

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
  }

  override fun getItemCount(): Int = 1

  override fun onViewAttachedToWindow(holder: ViewHolder) {
    super.onViewAttachedToWindow(holder)
    (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).apply {
      isFullSpan = true
      width = holder.itemView.context.resources.displayMetrics.widthPixels
      holder.itemView.layoutParams = this
    }
  }

  inner class Type0ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    init {
      val recyclerView = itemView.findViewById<RecyclerView>(R.id.top_list)
      recyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
      LinearSnapHelper().apply {
        attachToRecyclerView(recyclerView)
      }
      recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        /**
         * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
         * an item.
         *
         *
         * This new ViewHolder should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
         *
         *
         * The new ViewHolder will be used to display items of the adapter using
         * [.onBindViewHolder]. Since it will be re-used to display
         * different items in the data set, it is a good idea to cache references to sub views of
         * the View to avoid unnecessary [View.findViewById] calls.
         *
         * @param parent The ViewGroup into which the new View will be added after it is bound to
         * an adapter position.
         * @param viewType The view type of the new View.
         *
         * @return A new ViewHolder that holds a View of the given view type.
         * @see .getItemViewType
         * @see .onBindViewHolder
         */
        override fun onCreateViewHolder(
          parent: ViewGroup,
          viewType: Int
        ): RecyclerView.ViewHolder = Type3ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))

        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * update the contents of the [ViewHolder.itemView] to reflect the item at the given
         * position.
         *
         *
         * Note that unlike [android.widget.ListView], RecyclerView will not call this method
         * again if the position of the item changes in the data set unless the item itself is
         * invalidated or the new position cannot be determined. For this reason, you should only
         * use the `position` parameter while acquiring the related data item inside
         * this method and should not keep a copy of it. If you need the position of an item later
         * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
         * have the updated adapter position.
         *
         * Override [.onBindViewHolder] instead if Adapter can
         * handle efficient partial bind.
         *
         * @param holder The ViewHolder which should be updated to represent the contents of the
         * item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        override fun getItemCount(): Int = 5

        /**
         * Called when a view created by this adapter has been attached to a window.
         *
         *
         * This can be used as a reasonable signal that the view is about to be seen
         * by the user. If the adapter previously freed any resources in
         * [onViewDetachedFromWindow][.onViewDetachedFromWindow]
         * those resources should be restored here.
         *
         * @param holder Holder of the view being attached
         */
        override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
          super.onViewAttachedToWindow(holder)
          val lp = holder.itemView.layoutParams
          val width = holder.itemView.context.resources.displayMetrics.widthPixels / 1.5
          lp.width = width.toInt()
          lp.height = (width / 3 * 4).toInt()
          holder.itemView.layoutParams = lp
        }
      }
    }
  }

  inner class Type3ViewHolder(view: View): RecyclerView.ViewHolder(view) {}
}