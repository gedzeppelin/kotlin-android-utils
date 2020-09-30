package com.github.gedzeppelin.kotlinutils.adapter

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.github.gedzeppelin.kotlinutils.widget.SuspendableView

class SuspendableSectionAdapter(
    private val suspendableViews: List<SuspendableView<out Any>>
) : RecyclerView.Adapter<SuspendableSectionAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: SuspendableView<out Any>) : RecyclerView.ViewHolder(itemView)

    init {
        suspendableViews.forEach {
            it.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    }

    override fun getItemCount(): Int = suspendableViews.size

    override fun getItemViewType(position: Int): Int = position

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(suspendableViews[viewType])
}