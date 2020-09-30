@file:Suppress("unused")

package com.github.gedzeppelin.kotlinutils.delegate

import android.view.View
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.github.gedzeppelin.kotlinutils.R
import com.github.gedzeppelin.kotlinutils.validator.PbtnValidator
import com.github.gedzeppelin.kotlinutils.validator.State
import com.github.gedzeppelin.kotlinutils.validator.Validator
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

abstract class NullableFieldDelegate<T : Any, S : Any>(
    protected val validatorProp: KProperty0<Validator<out View>>,
    protected val tag: String,
    protected val callback: ((payload: S?) -> Boolean),
    protected val clazz: KClass<S>,
    lifecycle: Lifecycle
) : ReadWriteProperty<T, S?> {
    protected lateinit var validator: Validator<out View>
    private val isInitialized: Boolean get() = ::validator.isInitialized

    init {
        lifecycle.addObserver(object: LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() = initialize()
        })
    }

    protected abstract fun initValidator()

    private fun initialize() {
        if (!isInitialized) initValidator()
    }

    override operator fun getValue(thisRef: T, property: KProperty<*>): S? {
        return if (isInitialized) {
            validator.getField(tag)
        } else {
            initialize()
            validator.getField(tag)
        }
    }

    override operator fun setValue(thisRef: T, property: KProperty<*>, value: S?) {
        if (isInitialized) {
            validator.setField(tag, value)
        } else {
            initialize()
            validator.setField(tag, value)
        }
    }
}

class NullableFieldDelegateInt<T : Any, S : Any>(
    private val error: Int,
    validatorProp: KProperty0<Validator<out View>>,
    tag: String,
    callback: ((payload: S?) -> Boolean),
    clazz: KClass<S>,
    lifecycle: Lifecycle
) : NullableFieldDelegate<T, S>(validatorProp, tag, callback, clazz, lifecycle) {
    override fun initValidator() {
        validator = validatorProp.get()
        validator.addField(tag, error, null, State.ASLEEP, callback)
    }
}

class NullableFieldDelegateString<T : Any, S : Any>(
    private val error: String,
    validatorProp: KProperty0<PbtnValidator>,
    tag: String,
    callback: ((payload: S?) -> Boolean),
    clazz: KClass<S>,
    lifecycle: Lifecycle
) : NullableFieldDelegate<T, S>(validatorProp, tag, callback, clazz, lifecycle) {
    override fun initValidator() {
        validator = validatorProp.get()
        validator.addField(tag, error, null, State.ASLEEP, callback)
    }
}

class NullableFieldLoaderInt<T : Any, S : Any>(
    private val error: Int,
    private val validatorProp: KProperty0<Validator<out View>>,
    private val callback: ((payload: S?) -> Boolean),
    private val clazz: KClass<S>,
    private val lifecycle: Lifecycle
) {
    operator fun provideDelegate(thisRef: T, prop: KProperty<*>): ReadWriteProperty<T, S?> =
        NullableFieldDelegateInt(error, validatorProp, prop.name, callback, clazz, lifecycle)
}

class NullableFieldLoaderString<T : Any, S : Any>(
    private val error: String,
    private val validatorProp: KProperty0<PbtnValidator>,
    private val callback: ((payload: S?) -> Boolean),
    private val clazz: KClass<S>,
    private val lifecycle: Lifecycle
) {
    operator fun provideDelegate(thisRef: T, prop: KProperty<*>): ReadWriteProperty<T, S?> =
        NullableFieldDelegateString(error, validatorProp, prop.name, callback, clazz, lifecycle)
}

inline fun <T : Fragment, reified S : Any> T.nullableField(
    validatorProp: KProperty0<PbtnValidator>,
    noinline callback: (payload: S?) -> Boolean = { it != null }
) = NullableFieldLoaderInt<T, S>(R.string.delegate__custom_validator__default_error, validatorProp, callback, S::class, lifecycle)

inline fun <T : Fragment, reified S : Any> T.nullableField(
    prop: KProperty0<PbtnValidator>,
    error: Int,
    noinline callback: (payload: S?) -> Boolean = { it != null }
) = NullableFieldLoaderInt<T, S>(error, prop, callback, S::class, lifecycle)

inline fun <T : Fragment, reified S : Any> T.nullableField(
    validatorProp: KProperty0<PbtnValidator>,
    error: String,
    noinline callback: (payload: S?) -> Boolean = { it != null }
) = NullableFieldLoaderString<T, S>(error, validatorProp, callback, S::class, lifecycle)

inline fun <T : ComponentActivity, reified S : Any> T.nullableField(
    validatorProp: KProperty0<PbtnValidator>,
    noinline callback: (payload: S?) -> Boolean = { it != null }
) = NullableFieldLoaderInt<T, S>(R.string.delegate__custom_validator__default_error, validatorProp, callback, S::class, lifecycle)

inline fun <T : ComponentActivity, reified S : Any> T.nullableField(
    prop: KProperty0<PbtnValidator>,
    error: Int,
    noinline callback: (payload: S?) -> Boolean = { it != null }
) = NullableFieldLoaderInt<T, S>(error, prop, callback, S::class, lifecycle)

inline fun <T : ComponentActivity, reified S : Any> T.nullableField(
    validatorProp: KProperty0<PbtnValidator>,
    error: String,
    noinline callback: (payload: S?) -> Boolean = { it != null }
) = NullableFieldLoaderString<T, S>(error, validatorProp, callback, S::class, lifecycle)