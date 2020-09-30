package com.github.gedzeppelin.kotlinutils.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.gedzeppelin.kotlinutils.adapter.SuspendableSectionAdapter
import com.github.gedzeppelin.kotlinutils.databinding.SuspendFragmentTlyVp2Binding
import com.github.gedzeppelin.kotlinutils.widget.SuspendableView
import com.google.android.material.tabs.TabLayoutMediator

typealias NamedSuspendableViews = Map<String, SuspendableView<out Any>>

abstract class SuspendableTabFragment : Fragment() {
    abstract fun getNamedViews(ctx: Context): NamedSuspendableViews

    private var _binding: SuspendFragmentTlyVp2Binding? = null
    private val binding get() = _binding!!

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SuspendFragmentTlyVp2Binding.inflate(inflater, container, false)
        return binding.root
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val namedViews = getNamedViews(requireContext())

        val names = namedViews.keys.toList()
        val views = namedViews.values.toList()

        try {
            val mRecyclerViewField = ViewPager2::class.java.getDeclaredField( "mRecyclerView" )
            mRecyclerViewField.isAccessible = true

            val recyclerView = mRecyclerViewField.get(binding.sfVpr2) as RecyclerView

            val mTouchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            mTouchSlopField.isAccessible = true

            val touchSlop = mTouchSlopField[recyclerView] as Int
            mTouchSlopField[recyclerView] = touchSlop * 5
        } catch (_: Exception) {
            Log.e("SuspendableTabFragment", "ViewPager2 sensibility could not be fixed")
        }

        binding.sfVpr2.adapter = SuspendableSectionAdapter(views)
        TabLayoutMediator(binding.sfTly, binding.sfVpr2) { tab, position -> tab.text = names[position] }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}