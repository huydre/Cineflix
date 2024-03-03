package com.example.cineflix.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cineflix.View.Fragments.TvEpisodeFragment
import com.example.cineflix.View.Fragments.TvSimilarFragment

class TabLayoutTvDetailsAdapter(
    fragmentManage: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManage, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) TvEpisodeFragment()
        else TvSimilarFragment()
    }
}