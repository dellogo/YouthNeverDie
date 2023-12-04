package com.example.youthneverdie.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.youthneverdie.fragment.CommunityFragment
import com.example.youthneverdie.fragment.HomeFragment
import com.example.youthneverdie.fragment.OpenbookFragment
import com.example.youthneverdie.fragment.SettingFragment

class FragmentAdapter (fragment : FragmentActivity) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> OpenbookFragment()
            2 -> CommunityFragment()
            else -> SettingFragment()
        }
    }
}