@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils.delegate

import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.github.gedzeppelin.kotlinutils.widget.SuspendableView

// SuspendView delegate.

@Suppress("UNCHECKED_CAST")
abstract class SuspendableViewDelegate<T : Any, S : SuspendableView<T>>(
    lifecycle: Lifecycle,
    private val block: S.() -> Unit
) : Lazy<S> {
    abstract val rootView: View?

    init {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() = initialize()
        })
    }

    private fun findSuspendableView(view: View): S? = when (view) {
        is SuspendableView<out Any> -> view as S
        is ViewGroup -> {
            view.children.forEach {
                val inner = findSuspendableView(it)
                if (inner != null) return inner
            }
            null
        }
        else -> null
    }

    private fun initialize() {
        if (!isInitialized()) {
            val rootView0 = rootView
            if (rootView0 != null) {
                val result = findSuspendableView(rootView0)
                if (result != null) {
                    cached = result
                    cached.apply(block)
                } else {
                    throw IllegalStateException("Could not find the suspendable view in the root view")
                }
            } else {
                throw IllegalStateException("Could not find the suspendable view in the root view")
            }
        }
    }

    private lateinit var cached: S

    override val value: S
        get() {
            if (!isInitialized()) initialize()
            return cached
        }

    override fun isInitialized(): Boolean = ::cached.isInitialized
}

class SuspendableViewDelegateFragment<F: Fragment, T : Any, S : SuspendableView<T>>(
    lifecycle: Lifecycle,
    block: S.() -> Unit,
    private val fragment: F
) : SuspendableViewDelegate<T, S>(lifecycle, block) {
    override val rootView: View?
        get() = fragment.view
}

class SuspendableViewDelegateActivity<A: ComponentActivity, T : Any, S : SuspendableView<T>>(
    lifecycle: Lifecycle,
    block: S.() -> Unit,
    private val activity: A
) : SuspendableViewDelegate<T, S>(lifecycle, block) {
    override val rootView: View?
        get() = activity.window.decorView.rootView
}

// Suspendable view delegate function helpers.

fun <T : Any, S : SuspendableView<T>, F : Fragment> F.suspendableView(block: S.() -> Unit): Lazy<S> =
    SuspendableViewDelegateFragment(lifecycle, block, this)

fun <T : Any, S : SuspendableView<T>, A: ComponentActivity> A.suspendableView(block: S.() -> Unit): Lazy<S> =
    SuspendableViewDelegateActivity(lifecycle, block, this)