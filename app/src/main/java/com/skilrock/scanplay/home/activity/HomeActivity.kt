package com.skilrock.scanplay.home.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.skilrock.scanplay.R
import com.skilrock.scanplay.base.activity.BaseActivity
import com.skilrock.scanplay.databinding.ActivityHomeBinding
import com.skilrock.scanplay.home.adapter.HomeViewPagerAdapter
import com.skilrock.scanplay.home.sheet.LogoutSheet
import com.skilrock.scanplay.login.activity.LoginActivity
import com.skilrock.scanplay.utility.hideKeyboard
import com.skilrock.scanplay.utility.performLogoutCleanUp
import com.skilrock.scanplay.utility.visibilityGone
import com.skilrock.scanplay.utility.visibilityVisible

/**
 * Created by Rajneesh Kumar Sharma on 27 Jan 2023.
 * rajneeshk.sharma@skilrock.com
 * Skilrock Technologies
 */

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewPagerAdapter: HomeViewPagerAdapter
    private var mSelectedTabIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setViewPagerAndTabs()
        setPageChangeListener()
        setViewModel()
        setClickListeners()
        userLogoutBtnVisibility()
    }

    private fun userLogoutBtnVisibility() {
        if (checkIfLoggedIn()) {
            binding.ivLogout.visibilityVisible()
        } else
            binding.ivLogout.visibilityGone()
    }

    private fun initializeBinding() {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setViewModel() {}

    private fun setViewPagerAndTabs() {
        viewPagerAdapter                = HomeViewPagerAdapter(this)
        binding.viewPagerHome.adapter   = viewPagerAdapter
    }

    private fun setPageChangeListener() {
        binding.viewPagerHome.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mSelectedTabIndex = position
                animateTab(position)
            }
        })
    }

    private fun animateTab(position: Int) {
        val selectedColor   = ContextCompat.getColor(this, R.color.white)
        val unSelectedColor = ContextCompat.getColor(this, R.color.brownishAppGrey)
        val selectedBg      = R.drawable.selected_tab_button_bg
        val unSelectedBg    = R.drawable.unselected_tab_button_bg

        with(binding) {
            if (position == 0) {
                btnDeposit.animate().scaleX(0.9F).scaleY(0.9F).setDuration(100).withEndAction {
                    btnDeposit.setTextColor(selectedColor)
                    btnDeposit.setBackgroundResource(selectedBg)
                    btnDeposit.animate().scaleX(1F).scaleY(1F).duration = 100
                    btnWithdrawal.setTextColor(unSelectedColor)
                    btnWithdrawal.setBackgroundResource(unSelectedBg)
                    btnWithdrawal.animate().scaleX(0.9F).scaleY(0.9F).duration = 50
                }

            } else {
                btnWithdrawal.animate().scaleX(0.9F).scaleY(0.9F).setDuration(100).withEndAction {
                    btnWithdrawal.setTextColor(selectedColor)
                    btnWithdrawal.setBackgroundResource(selectedBg)
                    btnWithdrawal.animate().scaleX(1F).scaleY(1F).duration = 100
                    btnDeposit.setTextColor(unSelectedColor)
                    btnDeposit.setBackgroundResource(unSelectedBg)
                    btnDeposit.animate().scaleX(0.9F).scaleY(0.9F).duration = 50

                }
            }
        }
    }

    private fun setClickListeners() {
        with(binding) {

            btnDeposit.setOnClickListener {
                if (mSelectedTabIndex != 0) {
                    viewPagerHome.setCurrentItem(0, true)
                    animateTab(0)
                }
            }

            btnWithdrawal.setOnClickListener {
                if (mSelectedTabIndex != 1) {
                    btnWithdrawal.hideKeyboard()
                    viewPagerHome.setCurrentItem(1, true)
                    animateTab(1)
                }
            }

            ivLogout.setOnClickListener {
                val bottomSheetFragment = LogoutSheet(::onLogoutCallback)
                bottomSheetFragment.show(supportFragmentManager, LogoutSheet.TAG)
            }
        }
    }

    private fun onLogoutCallback() {
        performLogoutCleanUp(this, Intent(this, LoginActivity::class.java))
    }
}