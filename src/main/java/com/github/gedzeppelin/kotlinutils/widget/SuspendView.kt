@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.gedzeppelin.kotlinutils.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.github.gedzeppelin.kotlinutils.R
import com.github.gedzeppelin.kotlinutils.databinding.SuspendViewBinding

class SuspendView<T : Any> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : SuspendableView<T>(context, attrs, defStyleAttr, defStyleRes) {
    private val binding = SuspendViewBinding.inflate(LayoutInflater.from(context), this)
    private val _firstState: SuspendableViewState

    override lateinit var childView: View
    override val loadingView: ProgressBar
        get() = binding.pbrLoading

    override val errorView: LinearLayout
        get() = binding.error.llyError
    override val retryButton: ProgressButton
        get() = binding.error.pbtnError

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SuspendView, defStyleAttr, defStyleRes)

        initView(attributes, binding.error.tvwError, false)

        // Initial loading state.
        val initialStatus = attributes.getInt(R.styleable.SuspendView_initialState, 1)
        _firstState = SuspendableViewState.values()[initialStatus]

        attributes.recycle()
    }

    override fun onAttachedToWindow() {
        if (childCount == 3) {
            super.onAttachedToWindow()
            childView = getChildAt(2)
            state = _firstState
        } else {
            throw ChildCountException(javaClass.name, childCount)
        }
    }
}