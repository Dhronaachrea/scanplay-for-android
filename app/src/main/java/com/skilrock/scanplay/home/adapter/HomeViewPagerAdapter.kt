package com.skilrock.scanplay.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skilrock.scanplay.home.fragment.HomeDepositFragment
import com.skilrock.scanplay.home.fragment.WithdrawalFragment

class HomeViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
        //static
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            HomeDepositFragment()
        } else {
            WithdrawalFragment()
        }
    }
}