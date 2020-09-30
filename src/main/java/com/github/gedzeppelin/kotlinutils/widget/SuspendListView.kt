package com.github.gedzeppelin.kotlinutils.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.gedzeppelin.kotlinutils.R
import com.github.gedzeppelin.kotlinutils.adapter.MutableListAdapter
import com.github.gedzeppelin.kotlinutils.databinding.SuspendListViewBinding

@Suppress("unused")
class SuspendListView<T : Any> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : SuspendableView<List<T>>(context, attrs, defStyleAttr, defStyleRes), SwipeRefreshLayout.OnRefreshListener {
    private val binding = SuspendListViewBinding.inflate(LayoutInflater.from(context), this)

    override val childView: SwipeRefreshLayout
        get() = binding.srlChild

    override val errorView: LinearLayout
        get() = binding.error.llyError
    override val loadingView: ProgressBar
        get() = binding.pbrLoading
    override val retryButton: ProgressButton
        get() = binding.error.pbtnError

    val recyclerView: RecyclerView
        get() = binding.rvwChildFilled

    @Suppress("UNCHECKED_CAST")
    var adapter: MutableListAdapter<T, out RecyclerView.ViewHolder>?
        get() = binding.rvwChildFilled.adapter as? MutableListAdapter<T, out RecyclerView.ViewHolder>?
        set(value) {
            if (value != null) {
                value.onDataSetChanged = { itemCount ->
                    if (itemCount == 0 && binding.vsrChildBoth.currentView == binding.rvwChildFilled) binding.vsrChildBoth.showNext()
                    else if (itemCount > 0 && binding.vsrChildBoth.currentView == binding.svwChildEmpty) binding.vsrChildBoth.showPrevious()
                }
            }
            binding.rvwChildFilled.adapter = value
        }

    init {
        val parentAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SuspendView, 0, 0)
        initView(parentAttributes, binding.error.tvwError)
        parentAttributes.recycle()

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SuspendListView, defStyleAttr, defStyleRes)

        val defInnerPadding = resources.getDimensionPixelSize(R.dimen.srs__default_inner_padding)
        val innerPadding = attributes.getDimensionPixelSize(R.styleable.SuspendListView_innerPadding, defInnerPadding)
        binding.rvwChildFilled.setPadding(innerPadding, innerPadding, innerPadding, innerPadding)

        binding.tvwChildEmpty.text = attributes.getString(R.styleable.SuspendListView_emptyText)
            ?: context.getString(R.string.tvw__suspend_section_list__default_empty)
        attributes.recycle()

        val linearLayoutManager = LinearLayoutManager(context)
        binding.rvwChildFilled.layoutManager = linearLayoutManager
        //binding.rvwChildFilled.addItemDecoration(DividerDecorator(context))

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