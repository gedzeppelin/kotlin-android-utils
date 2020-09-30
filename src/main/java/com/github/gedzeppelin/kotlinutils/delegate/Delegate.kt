@file:Suppress("unused", "UNCHECKED_CAST")

package com.github.gedzeppelin.kotlinutils.delegate

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.github.gedzeppelin.kotlinutils.widget.SuspendableView
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface ReadOnlyDelegateProvider<T, S> {
    operator fun provideDelegate(thisRef: T, property: KProperty<*>): ReadOnlyProperty<T, S>
}

interface ReadWriteDelegateProvider<T, S> {
    operator fun provideDelegate(thisRef: T, property: KProperty<*>): ReadWriteProperty<T, S>
}

const val BUNDLE_ERROR = "Payload was not sent into the bundle or is not a valid bundle value"
const val STRING_ERROR = "String value was not sent into the bundle"

abstract class BundleValueDelegate<T : Any>(private val key: String) : Lazy<T?> {
    protected abstract val bundle: Bundle?

    private var isInitialized: Boolean = false
    private var payload: T? = null

    override val value: T?
        get() {
            if (!isInitialized()) {
                payload = bundle?.get(key) as? T
                isInitialized = true
            }
            return payload
        }

    override fun isInitialized(): Boolean = isInitialized
}

abstract class RequireBundleValueDelegate<T : Any>(private val key: String) : Lazy<T> {
    protected abstract val bundle: Bundle
    private lateinit var payload: T

    final override val value: T
        get() {
            if (!isInitialized()) payload = bundle.get(key) as? T ?: throw AssertionError(BUNDLE_ERROR)
            return payload
        }

    final override fun isInitialized(): Boolean = ::payload.isInitialized
}

// Required bundled parcelable delegates.

class RequireBundleValueFragment<T : Any>(
    private val fragment: Fragment,
    key: String
) : RequireBundleValueDelegate<T>(key) {
    override val bundle: Bundle
        get() = fragment.requireArguments()
}

class RequireBundleValueActivity<T : Any>(
    private val activity: Activity,
    key: String
) : RequireBundleValueDelegate<T>(key) {
    override val bundle: Bundle
        get() = activity.intent?.extras ?: throw AssertionError(BUNDLE_ERROR)
}

// Bundled parcelable delegates.

class BundleValueFragment<T : Any>(
    private val fragment: Fragment,
    key: String
) : BundleValueDelegate<T>(key) {
    override val bundle: Bundle?
        get() = fragment.arguments
}

class BundleValueActivity<T : Any>(
    private val activity: Activity,
    key: String
) : BundleValueDelegate<T>(key) {
    override val bundle: Bundle?
        get() = activity.intent?.extras
}

// Drawable delegate.

class DrawableDelegate(
    private val fragment: Fragment,
    private val resId: Int
) : Lazy<Drawable?> {
    private var isInitialized = false
    private var cached: Drawable? = null
    override val value: Drawable?
        get() = if (isInitialized) {
            cached
        } else {
            val output = ResourcesCompat.getDrawable(fragment.resources, resId, null)
            isInitialized = true
            output
        }

    override fun isInitialized() = isInitialized
}

fun <T : Activity> T.requireString(key: String): String =
    intent.extras?.getString(key) ?: throw AssertionError(STRING_ERROR)

fun <T : Fragment> T.requireString(key: String): String =
    requireArguments().getString(key) ?: throw AssertionError(STRING_ERROR)

// Bundled delegate function helpers.

fun <T : Fragment, S : Any> T.bundleValue(key: String): Lazy<S?> =
    BundleValueFragment(this, key)

fun <T : Fragment, S : Any> T.requireBundleValue(key: String): Lazy<S> =
    RequireBundleValueFragment(this, key)

fun <T : Activity, S : Any> T.bundleValue(key: String): Lazy<S?> =
    BundleValueActivity(this, key)

fun <T : Activity, S : Any> T.requireBundleValue(key: String): Lazy<S> =
    RequireBundleValueActivity(this, key)

// Drawable delegate function helpers.

fun <T : Fragment> T.drawable(resId: Int): Lazy<Drawable?> =
    DrawableDelegate(this, resId)

// Named view function helpers.

fun namedViewsOf(vararg pairs: Pair<String, SuspendableView<out Any>>): Map<String, SuspendableView<out Any>> = mapOf(*pairs)

fun <T : Fragment> T.namedViewsOf(vararg pairs: Pair<Int, SuspendableView<out Any>>): Map<String, SuspendableView<out Any>> {
    val result = pairs.map { getString(it.first) to it.second }.toTypedArray()
    return mapOf(*result)
}

fun <T : Activity> T.namedViewsOf(vararg pairs: Pair<Int, SuspendableView<out Any>>): Map<String, SuspendableView<out Any>> {
    val result = pairs.map { getString(it.first) to it.second }.toTypedArray()
    return mapOf(*result)
}