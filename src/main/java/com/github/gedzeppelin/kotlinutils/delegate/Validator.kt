@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils.delegate

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.github.gedzeppelin.kotlinutils.validator.PbtnValidator
import com.github.gedzeppelin.kotlinutils.validator.impl0.ProgressButtonValidator
import com.github.gedzeppelin.kotlinutils.widget.ProgressButton

abstract class PbtnValidatorIdDelegate(
    private val block: PbtnValidator.() -> Unit,
    private val pbtnBlock: () -> ProgressButton,
    lifecycle: Lifecycle
) : Lazy<PbtnValidator> {
    abstract val context: Context

    private lateinit var validator: PbtnValidator

    init {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() = initialize()
        })
    }

    protected fun initialize() {
        if (!isInitialized()) {
            val progressButton = pbtnBlock()
            validator = ProgressButtonValidator(context, progressButton)
            validator.apply(block)
        }
    }

    override val value: PbtnValidator
        get() {
            if (!isInitialized()) initialize()
            return validator
        }

    override fun isInitialized(): Boolean = ::validator.isInitialized
}

abstract class PbtnValidatorRecursiveDelegate(
    private val block: PbtnValidator.() -> Unit,
    lifecycle: Lifecycle
) : Lazy<PbtnValidator> {
    abstract val context: Context
    abstract val rootView: View

    private lateinit var validator: PbtnValidator

    init {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() = initialize()
        })
    }

    protected fun initialize() {
        if (!isInitialized()) {
            val progressButton = findTarget(rootView)
            if (progressButton != null) {
                progressButton.ignoreByDelegate = true
                validator = ProgressButtonValidator(context, progressButton)
                validator.apply(block)
            } else {
                throw IllegalStateException("Could not find the progress button in the root view")
            }
        }
    }

    private fun findTarget(view: View): ProgressButton? = when (view) {
        is ProgressButton -> view
        is ViewGroup -> {
            var result: ProgressButton? = null
            for (view0 in view.children) {
                val inner = findTarget(view0)
                if (inner != null && !inner.ignoreByDelegate) result = inner
            }
            result
        }
        else -> null
    }

    override val value: PbtnValidator
        get() {
            if (!isInitialized()) initialize()
            return validator
        }

    override fun isInitialized(): Boolean = ::validator.isInitialized
}

class PbtnValidatorRecursiveDelegateActivity<T : ComponentActivity>(
    block: PbtnValidator.() -> Unit,
    private val activity: T
) : PbtnValidatorRecursiveDelegate(block, activity.lifecycle) {
    override val context: Context
        get() = activity
    override val rootView: View
        get() = activity.window.decorView.rootView
}

class PbtnValidatorRecursiveDelegateFragment<T : Fragment>(
    block: PbtnValidator.() -> Unit,
    private val fragment: T
) : PbtnValidatorRecursiveDelegate(block, fragment.lifecycle) {
    override val context: Context
        get() = fragment.requireContext()
    override val rootView: View
        get() = fragment.requireView()
}

fun <T : Fragment> T.progressButtonValidator(
    block: ProgressButtonValidator.() -> Unit
) = PbtnValidatorRecursiveDelegateFragment(block, this)

fun <T : ComponentActivity> T.progressButtonValidator(
    block: ProgressButtonValidator.() -> Unit
) = PbtnValidatorRecursiveDelegateActivity(block, this)