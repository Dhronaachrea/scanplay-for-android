package com.skilrock.scanplay.base.fragment

import android.content.Context
import androidx.fragment.app.Fragment
import com.skilrock.scanplay.home.activity.HomeActivity

abstract class BaseFragment : Fragment() {

    lateinit var master: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity)
            master = context
    }
}