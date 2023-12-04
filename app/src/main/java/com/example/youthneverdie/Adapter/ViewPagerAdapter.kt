package com.example.youthneverdie.Adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.youthneverdie.fragment.CommunityFragment
import com.example.youthneverdie.fragment.LicenseFragment
import com.example.youthneverdie.fragment.WorkFragment

class ViewPagerAdapter(fragmentActivity: CommunityFragment): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> WorkFragment()
            else -> LicenseFragment()
        }
    }
}