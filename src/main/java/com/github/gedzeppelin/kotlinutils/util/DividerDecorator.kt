package com.github.gedzeppelin.kotlinutils.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.R.attr.listDivider

class DividerDecorator(context: Context) : RecyclerView.ItemDecoration() {
    private val mDrawable: Drawable?

    init {
        val attrs = intArrayOf(listDivider)
        val a = context.obtainStyledAttributes(attrs)
        mDrawable = a.getDrawable(0)
        a.recycle()
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (mDrawable != null) {
            canvas.save()
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight


            val childCount = parent.childCount
            for (i in 0 until childCount - 1) {
                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin
                val bottom = top + mDrawable.intrinsicHeight

                mDrawable.setBounds(left, top, right, bottom)
                mDrawable.draw(canvas)
            }
            canvas.restore()
        } else {
            throw IllegalArgumentException("Drawable cannot be null.")
        }
    }
}