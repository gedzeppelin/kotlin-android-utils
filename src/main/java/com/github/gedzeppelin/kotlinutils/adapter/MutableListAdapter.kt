package com.github.gedzeppelin.kotlinutils.adapter

import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KProperty1

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class MutableListAdapter<T : Any, S : RecyclerView.ViewHolder>(
    private val identifier: KProperty1<T, Any>? = null
) : RecyclerView.Adapter<S>() {
    val dataSet: MutableList<T> = mutableListOf()
    var onDataSetChanged: ((itemCount: Int) -> Unit)? = null

    final override fun getItemCount(): Int = dataSet.size

    fun replaceList(newList: List<T>) {
        dataSet.clear()
        dataSet.addAll(newList)
        notifyDataSetChanged()
        onDataSetChanged?.invoke(itemCount)
    }

    fun addItem(element: T, idx: Int = 0): T {
        dataSet.add(idx, element)
        notifyItemInserted(idx)
        onDataSetChanged?.invoke(itemCount)
        return element
    }

    fun modifyItem(element: T, idx: Int? = null) {
        if (idx == null) {
            val index = indexOf(element)
            dataSet[index] = element
            notifyItemChanged(index)
        } else {
            dataSet[idx] = element
            notifyItemChanged(idx)
        }
    }

    fun removeItem(element: T): T? {
        val index = indexOf(element)
        val result = dataSet.removeAt(index)
        notifyItemRemoved(index)
        onDataSetChanged?.invoke(itemCount)
        return result
    }

    fun removeItemAt(idx: Int): T? {
        val result = dataSet.removeAt(idx)
        notifyItemRemoved(idx)
        onDataSetChanged?.invoke(itemCount)
        return result
    }

    fun tryAddItem(element: T): Boolean {
        val index = indexOf(element)
        if (index == -1) {
            addItem(element)
            return true
        }
        return false
    }

    fun tryModifyItem(element: T): Boolean {
        val index = indexOf(element)
        if (index >= 0) {
            modifyItem(element, index)
            return true
        }
        return false
    }

    fun tryRemoveItem(element: T): Boolean {
        val index = indexOf(element)
        if (index >= 0) {
            removeItemAt(index)
            return true
        }
        return false
    }

    fun addOrModifyItem(element: T): T {
        val index = indexOf(element)
        if (index == -1) addItem(element)
        else modifyItem(element, index)
        return element
    }

    fun getItemAt(idx: Int): T = dataSet[idx]

    fun tryGetItemAt(idx: Int): T? {
        return try {
            dataSet[idx]
        } catch (e: Exception) {
            null
        }
    }

    fun indexOf(element: T): Int {
        return if (identifier != null) {
            val prop0 = identifier.get(element)
            dataSet.forEachIndexed { idx, e ->
                val prop1 = identifier.get(e)
                if (prop0 == prop1) return idx
            }
            return -1
        } else {
            dataSet.indexOf(element)
        }
    }

    fun contains(element: T): Boolean = indexOf(element) >= 0
}