package com.github.gedzeppelin.kotlinutils.util

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import com.github.gedzeppelin.kotlinutils.ACTION_BUNDLE_KEY
import com.github.gedzeppelin.kotlinutils.Action
import com.github.gedzeppelin.kotlinutils.PAYLOAD_BUNDLE_KEY
import com.github.gedzeppelin.kotlinutils.SUCCESSFUL_ACTION_FRAGMENT_RESULT_KEY
import com.github.gedzeppelin.kotlinutils.adapter.ModelAdapter
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor

interface ModelAdapterHalf<T> where  T : Any, T : Parcelable {
    val action: Action get() = if (isFirst) Action.DEACTIVATE else Action.ACTIVATE

    val fragment: Fragment
    val isFirst: Boolean
}

class ModelAdapterPair<T, V>(
    fragment: Fragment,
    adapterClazz: KClass<V>,
    vararg args: Any?
) where T : Any, T : Parcelable, V : ModelAdapter<T, out RecyclerView.ViewHolder>, V : ModelAdapterHalf<T> {
    val first: V
    val second: V

    init {
        val adapterConstructor = adapterClazz.primaryConstructor as KFunction<V>

        val args0 = mutableListOf<Any?>()
        val args1 = mutableListOf<Any?>()

        args.forEach { arg ->
            if (arg is Pair<Any?, Any?>) {
                args0.add(arg.first)
                args1.add(arg.second)
            } else {
                args0.add(arg)
                args1.add(arg)
            }
        }

        first = adapterConstructor.call(fragment, true, *args0.toTypedArray())
        second = adapterConstructor.call(fragment, false, *args1.toTypedArray())

        fragment.setFragmentResultListener(SUCCESSFUL_ACTION_FRAGMENT_RESULT_KEY) { _, bundle ->
            val action: Action = bundle.getParcelable(ACTION_BUNDLE_KEY)
                ?: throw AssertionError("The action type was not sent")
            val payload: T = bundle.getParcelable(PAYLOAD_BUNDLE_KEY)
                ?: throw AssertionError("The payload result was not sent")

            when (action) {
                Action.CREATE -> first.addItem(payload)
                Action.MODIFY -> {
                    if (first.contains(payload)) first.modifyItem(payload)
                    else if (second.contains(payload)) second.modifyItem(payload)
                }
                Action.ACTIVATE -> {
                    first.addItem(payload)
                    second.removeItem(payload)
                }
                Action.DEACTIVATE -> {
                    first.removeItem(payload)
                    second.addItem(payload)
                }
                Action.DELETE -> {
                    if (first.contains(payload)) first.removeItem(payload)
                    else if (second.contains(payload)) second.removeItem(payload)
                }
            }
        }
    }
}

class ModelAdapterPairDelegate<T, V>(
    private val fragment: Fragment,
    private val clazz: KClass<V>,
    private vararg val args: Any?
) : Lazy<ModelAdapterPair<T, V>> where T : Any, T : Parcelable, V : ModelAdapter<T, out RecyclerView.ViewHolder>, V : ModelAdapterHalf<T> {
    private lateinit var cached: ModelAdapterPair<T, V>

    override val value: ModelAdapterPair<T, V>
        get() {
            if (!isInitialized()) cached = ModelAdapterPair(fragment, clazz, *args)
            return cached
        }

    override fun isInitialized(): Boolean = ::cached.isInitialized
}

fun <T, V> Fragment.modelAdapterPair(
    clazz: KClass<V>,
    vararg args: Any?
): Lazy<ModelAdapterPair<T, V>> where T : Any, T : Parcelable, V : ModelAdapter<T, out RecyclerView.ViewHolder>, V : ModelAdapterHalf<T> =
    ModelAdapterPairDelegate(this, clazz, *args)