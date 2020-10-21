package com.github.gedzeppelin.kotlinutils.adapter

import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KProperty1

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class ModelAdapter<T : Any, S : RecyclerView.ViewHolder>(
    private val identifier: KProperty1<T, Any>? = null
) : RecyclerView.Adapter<S>() {
    val items: MutableList<T> = mutableListOf()
    var onItemsChanged: ((itemCount: Int) -> Unit)? = null

    final override fun getItemCount(): Int = items.size

    fun replaceList(newList: List<T>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
        onItemsChanged?.invoke(itemCount)
    }

    fun addItem(element: T, idx: Int = 0): T {
        items.add(idx, element)
        notifyItemInserted(idx)
        onItemsChanged?.invoke(itemCount)
        return element
    }

    fun modifyItem(element: T, idx: Int? = null) {
        if (idx == null) {
            val index = indexOf(element)
            items[index] = element
            notifyItemChanged(index)
        } else {
            items[idx] = element
            notifyItemChanged(idx)
        }
    }

    fun removeItem(element: T): T? {
        val index = indexOf(element)
        val result = items.removeAt(index)
        notifyItemRemoved(index)
        onItemsChanged?.invoke(itemCount)
        return result
    }

    fun removeItemAt(idx: Int): T? {
        val result = items.removeAt(idx)
        notifyItemRemoved(idx)
        onItemsChanged?.invoke(itemCount)
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

    fun getItemAt(idx: Int): T = items[idx]

    fun tryGetItemAt(idx: Int): T? {
        return try {
            items[idx]
        } catch (e: Exception) {
            null
        }
    }

    fun indexOf(element: T): Int {
        return if (identifier != null) {
            val prop0 = identifier.get(element)
            items.forEachIndexed { idx, e ->
                val prop1 = identifier.get(e)
                if (prop0 == prop1) return idx
            }
            return -1
        } else {
            items.indexOf(element)
        }
    }

    fun contains(element: T): Boolean = indexOf(element) >= 0
}