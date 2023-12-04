package com.example.youthneverdie.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.youthneverdie.Adapter.ViewPagerAdapter
import com.example.youthneverdie.CommunitypostActivity
import com.example.youthneverdie.R
import com.example.youthneverdie.databinding.FragmentCommunityBinding
import com.google.android.material.tabs.TabLayoutMediator

class CommunityFragment : Fragment() {
    private val binding: FragmentCommunityBinding by lazy { FragmentCommunityBinding.inflate(layoutInflater) }

    private val tabTextList = listOf("취업 게시판", "자격증 게시판")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return (binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.writeBtn.setOnClickListener {
            val intent = Intent(getActivity(), CommunitypostActivity::class.java)
            startActivity(intent)
        }

        arguments?.let {
        }
        binding.viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = tabTextList[pos]
        }.attach()
    }
}