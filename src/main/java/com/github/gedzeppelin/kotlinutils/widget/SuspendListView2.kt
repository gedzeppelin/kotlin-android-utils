package com.github.gedzeppelin.kotlinutils.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.gedzeppelin.kotlinutils.R
import com.github.gedzeppelin.kotlinutils.adapter.ModelAdapter
import com.github.gedzeppelin.kotlinutils.databinding.SuspendListView2Binding
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Same as [SuspendListView] but with a [FloatingActionButton].
 */
@Suppress("unused")
class SuspendListView2<T : Any> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : SuspendableView<List<T>>(context, attrs, defStyleAttr, defStyleRes), SwipeRefreshLayout.OnRefreshListener {
    private val binding = SuspendListView2Binding.inflate(LayoutInflater.from(context), this)

    override val childView: CoordinatorLayout
        get() = binding.clyChild
    override val loadingView: ProgressBar
        get() = binding.pbrLoading

    override val errorView: LinearLayout
        get() = binding.error.llyError
    override val retryButton: ProgressButton
        get() = binding.error.pbtnError

    fun setOnFabClickListener(l: OnClickListener?) {
        binding.fabChildFilled.setOnClickListener(l)
    }

    fun setOnFabClickListener(l: ((View) -> Unit)?) {
        binding.fabChildFilled.setOnClickListener(l)
    }

    @Suppress("UNCHECKED_CAST")
    var adapter: ModelAdapter<T, out RecyclerView.ViewHolder>?
        get() = (binding.rvwChildFilled.adapter as? ModelAdapter<T, out RecyclerView.ViewHolder>?)
        set(value) {
            value?.onItemsChanged = { itemCount ->
                if (itemCount == 0 && binding.vsrChildBoth.currentView === binding.rvwChildFilled) {
                    binding.vsrChildBoth.showNext()
                } else if (itemCount > 0 && binding.vsrChildBoth.currentView === binding.svwChildEmpty) {
                    binding.vsrChildBoth.showPrevious()
                }
            }
            binding.rvwChildFilled.adapter = value
        }

    init {
        val parentAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SuspendView, 0, 0)
        // Error text.
        binding.error.tvwError.text =
            parentAttributes.getString(R.styleable.SuspendView_errorText) ?: context.getString(R.string.tvw__suspendable_view__default_error)
        // Error button text.
        binding.error.pbtnError.text =
            parentAttributes.getString(R.styleable.SuspendView_errorButtonText) ?: context.getString(R.string.pbtn__suspendable_view__default_text)
        retryButton.ignoreByDelegate = true
        // Initial loading state.
        state = SuspendableViewState.values()[parentAttributes.getInt(R.styleable.SuspendView_initialState, 1)]
        parentAttributes.recycle()

        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SuspendListView, 0, 0)
        binding.rvwChildFilled.setPadding(
            attributes.getDimensionPixelSize(
                R.styleable.SuspendListView_innerPadding,
                resources.getDimensionPixelSize(R.dimen.srs__default_inner_padding)
            )
        )
        binding.tvwChildEmpty.text = attributes.getString(R.styleable.SuspendListView_emptyText)
            ?: context.getString(R.string.tvw__suspend_section_list__default_empty)
        attributes.recycle()

        val linearLayoutManager = LinearLayoutManager(context)
        binding.rvwChildFilled.layoutManager = linearLayoutManager

        binding.rvwChildFilled.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                binding.srlChild.isEnabled = linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0
            }
        })
        binding.srlChild.setOnRefreshListener(this)
    }

    override fun onStateChanged(newState: SuspendableViewState) {
        super.onStateChanged(newState)
        binding.srlChild.isRefreshing = false
    }

    override fun onRefresh() = super.refresh(false)
}