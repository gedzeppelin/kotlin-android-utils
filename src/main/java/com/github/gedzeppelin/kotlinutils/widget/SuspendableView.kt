package com.github.gedzeppelin.kotlinutils.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.github.gedzeppelin.kotlinutils.R
import com.github.gedzeppelin.kotlinutils.Response
import kotlinx.coroutines.*

class RefreshException(blockName: String) : Exception("$blockName was not initialized. Try to set $blockName")

class ChildCountException(name: String, count: Int) : Exception("$name must have always only one child, current child count was ${count - 2}")

enum class SuspendableViewState {
    ERROR,
    LOADING,
    SUCCESS
}

/**
 * The view that is shown on SUCCESS state.
 *
 * @property state The current suspend state of [suspendBlock].
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class SuspendableView<T : Any> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    abstract val childView: View
    abstract val loadingView: View

    abstract val errorView: View
    abstract val retryButton: ProgressButton

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    var state: SuspendableViewState = SuspendableViewState.LOADING
        set(value) {
            onStateChanged(value)
            field = value
        }

    // Suspend callback method.
    var suspendBlock: (suspend () -> Response<T>)? = null
        set(value) {
            field = value
            refresh()
        }

    // Callbacks.
    var onSuccess: ((payload: T) -> Unit)? = null
    var onError: ((error: Response.Error) -> Unit)? = null
    var onComplete: (() -> Unit)? = null

    fun refresh(hard: Boolean = false) {
        val suspendBlock0 = suspendBlock

        if (suspendBlock0 != null) {
            if (hard) state = SuspendableViewState.LOADING

            scope.launch {
                state = when (val result = suspendBlock0.invoke()) {
                    is Response.Success -> {
                        onSuccess?.invoke(result.payload)
                        SuspendableViewState.SUCCESS
                    }
                    is Response.Error -> {
                        onError?.invoke(result)
                        SuspendableViewState.ERROR
                    }
                }
                onComplete?.invoke()
            }
        } else {
            val err = RefreshException(::suspendBlock.name)
            onError?.invoke(Response.Error(err))
            onComplete?.invoke()
            state = SuspendableViewState.ERROR
        }
    }

    protected open fun onStateChanged(newState: SuspendableViewState) {
        when (newState) {
            SuspendableViewState.LOADING -> {
                loadingView.visibility = View.VISIBLE
                childView.visibility = View.GONE
                errorView.visibility = View.GONE
            }
            SuspendableViewState.ERROR -> {
                loadingView.visibility = View.GONE
                childView.visibility = View.GONE
                errorView.visibility = View.VISIBLE
            }
            SuspendableViewState.SUCCESS -> {
                loadingView.visibility = View.GONE
                childView.visibility = View.VISIBLE
                errorView.visibility = View.GONE
            }
        }
        retryButton.isLoading = false
    }

    protected fun initView(attrs: TypedArray, tvwError: TextView, modifyState: Boolean = true) {
        // Error text.
        tvwError.text = attrs.getString(R.styleable.SuspendView_errorText)
            ?: context.getString(R.string.tvw__suspendable_view__default_error)

        // Error button text.
        retryButton.ignoreByDelegate = true
        retryButton.text = attrs.getString(R.styleable.SuspendView_errorButtonText)
            ?: context.getString(R.string.pbtn__suspendable_view__default_text)

        if (modifyState) {
            val initialStatus = attrs.getInt(R.styleable.SuspendView_initialState, 1)
            state = SuspendableViewState.values()[initialStatus]
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 3) {
            super.onLayout(changed, l, t, r, b)
        } else {
            throw ChildCountException(javaClass.name, childCount)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        retryButton.setOnClickListener(null)
        retryButton.setOnClickListener {
            retryButton.isLoading = true
            refresh()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scope.cancel()
        onSuccess = null
        onError = null
        onComplete = null
    }
}