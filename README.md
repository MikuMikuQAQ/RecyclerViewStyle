先看效果图：

![1661786885169](https://www.rainblog.cn/upload/2022/08/1661786885169.gif)

顶部由横向滚动的列表实现，中间有一个banner，主要布局是一个瀑布流StaggeredGridLayoutManager实现的一个列表，最后有一个footer来显示加载页。

如果用NestedScrollView来实现以上效果，就会出现瀑布流RecyclerView无限加载数据导致ANR，手动控制数据加载也可以，但会导致刷新列表时卡顿，这个卡顿会在数据加载越多而明显，因为瀑布流的Adapter不会再回收Item了，所以用户体验非常不好；这个时候我们就可以通过单RecyclerView+多ViewHolder来实现，由Adapter自己进行ViewHolder的回收。

#### 单RecyclerView+多ViewHolder实现思路

##### RecyclerView的思路
RecyclerView我们由StaggeredGridLayoutManager布局来进行实现：
```kotlin
    mRecyclerView = findViewById(R.id.recycler_view)
    mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
        mRecyclerView.layoutManager = this
    }
    mRecyclerView.adapter = DemoAdapter()
```

##### Adapter的思路
1. 创建好我们所需的几个ViewHolder，通过fun getItemViewType(position: Int): Int方法对不同的数据源进行归类来调用我们想要的ViewHolder，在fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder方法中通过viewType来返回我们定义的所需的ViewHolder：
```kotlin
    //这是我们定义的类型0代表顶部的列表、1代表中间的banner、2代表错位的瀑布流item、3代表加载item、4代表瀑布流普通的item
    override fun getItemViewType(position: Int): Int {
        Log.e("MainActivity", "getItemViewType: $position", )
        return when(position) {
            0 -> 0
            1 -> 1
            2 -> 2
            itemCount - 1 -> 3
            else -> 4
        }
    }
    
    //getItemViewType方法返回的类型都在这里消费
    //Type0ViewHolder顶部的列表
    //Type1ViewHolder中间的banner
    //Type2ViewHolder错位的瀑布流item
    //LoadMoreViewHolder加载item
    //Type3ViewHolder瀑布流普通的item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when(viewType) {
        0 -> Type0ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_top_list, parent, false))
        1 -> Type1ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))
        2 -> Type2ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))
        3 -> LoadMoreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_load_more, parent, false))
        else -> Type3ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))
    }
```

2. 由于我们用的是StaggeredGridLayoutManager布局，只有类型4能满足我们的布局需求外，其他的ViewHolder都不是我们想要的效果，这个时候就需要重写onViewAttachedToWindow方法对不同的ViewHolder进行处理来实现我们的效果：
```kotlin
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.layoutPosition
        when (holder) {
            is DemoAdapter.Type0ViewHolder -> {
                (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).apply {
                    isFullSpan = true
                    width = holder.itemView.context.resources.displayMetrics.widthPixels
                    holder.itemView.layoutParams = this
                }
            }
            is DemoAdapter.Type1ViewHolder -> {
                (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).apply {
                    isFullSpan = true
                    width = holder.itemView.context.resources.displayMetrics.widthPixels
                    height = width / 4
                    holder.itemView.layoutParams = this
                }
            }
            is DemoAdapter.Type2ViewHolder -> {
                val lp = holder.itemView.layoutParams
                val width = holder.itemView.context.resources.displayMetrics.widthPixels / 2
                lp.width = width
                lp.height = width / 3 * 5
                holder.itemView.layoutParams = lp
            }
            is DemoAdapter.Type3ViewHolder -> {
                val lp = holder.itemView.layoutParams
                val width = holder.itemView.context.resources.displayMetrics.widthPixels / 2
                lp.width = width
                lp.height = width / 3 * 4
                holder.itemView.layoutParams = lp
            }
            is DemoAdapter.LoadMoreViewHolder -> {
                (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            }
        }
    }
```
以上代码中通过判断ViewHolder的类型来进行不同的处理，在StaggeredGridLayoutManager布局中我们想要实现占满一整行的效果可以通过StaggeredGridLayoutManager.LayoutParams.isFullSpan = true来实现。
如果你用的GridLayoutManager可以通过重写SpanSizeLookup来实现：
```kotlin
    GridLayoutManager(this, 2).spanSizeLookup = object : SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (position < 2 || position == itemCount - 1) {
                2
            } else {
                1
            }
        }
    }
```

##### 贴上完整代码：
###### 1.xml
item_load_more.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#000"
    android:gravity="center"
    android:orientation="horizontal">

    <ProgressBar
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

</LinearLayout>
```
item_top_list.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/top_list"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

</FrameLayout>
```
item_card.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:padding="4dp"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <androidx.cardview.widget.CardView
        android:id="@+id/card_view"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_centerInParent="true"
        android:clickable="true"
        android:focusable="true"
        android:foreground="?attr/selectableItemBackgroundBorderless"
        app:cardCornerRadius="12dp"
        app:cardPreventCornerOverlap="true"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">

        <View
            android:background="@color/teal_700"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

    </androidx.cardview.widget.CardView>

</androidx.constraintlayout.widget.ConstraintLayout>
```

###### 2.adapter
DemoAdapter:
```kotlin
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class DemoAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when(viewType) {
        0 -> Type0ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_top_list, parent, false))
        1 -> Type1ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))
        2 -> Type2ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))
        3 -> LoadMoreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_load_more, parent, false))
        else -> Type3ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))
    }

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
    override fun getItemCount(): Int = 30

    /**
     * Return the view type of the item at `position` for the purposes
     * of view recycling.
     *
     *
     * The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * `position`. Type codes need not be contiguous.
     */
    override fun getItemViewType(position: Int): Int {
        Log.e("MainActivity", "getItemViewType: $position", )
        return when(position) {
            0 -> 0
            1 -> 1
            2 -> 2
            itemCount - 1 -> 3
            else -> 4
        }
    }

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
        val position = holder.layoutPosition
        when (holder) {
            is DemoAdapter.Type0ViewHolder -> {
                (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).apply {
                    isFullSpan = true
                    width = holder.itemView.context.resources.displayMetrics.widthPixels
                    holder.itemView.layoutParams = this
                }
            }
            is DemoAdapter.Type1ViewHolder -> {
                (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).apply {
                    isFullSpan = true
                    width = holder.itemView.context.resources.displayMetrics.widthPixels
                    height = width / 4
                    holder.itemView.layoutParams = this
                }
            }
            is DemoAdapter.Type2ViewHolder -> {
                val lp = holder.itemView.layoutParams
                val width = holder.itemView.context.resources.displayMetrics.widthPixels / 2
                lp.width = width
                lp.height = width / 3 * 5
                holder.itemView.layoutParams = lp
            }
            is DemoAdapter.Type3ViewHolder -> {
                val lp = holder.itemView.layoutParams
                val width = holder.itemView.context.resources.displayMetrics.widthPixels / 2
                lp.width = width
                lp.height = width / 3 * 4
                holder.itemView.layoutParams = lp
            }
            is DemoAdapter.LoadMoreViewHolder -> {
                (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            }
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

    inner class Type1ViewHolder(view: View): RecyclerView.ViewHolder(view) {}

    inner class Type2ViewHolder(view: View): RecyclerView.ViewHolder(view) {}

    inner class Type3ViewHolder(view: View): RecyclerView.ViewHolder(view) {}

    inner class LoadMoreViewHolder(view: View): RecyclerView.ViewHolder(view) {}

}
```
