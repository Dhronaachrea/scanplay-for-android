package com.skilrock.scanplay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.skilrock.scanplay.base.activity.BaseActivity
import com.skilrock.scanplay.databinding.ActivitySplashBinding
import com.skilrock.scanplay.home.activity.HomeActivity
import com.skilrock.scanplay.login.activity.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        proceedToHomeScreen()
    }

    private fun initializeBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun proceedToHomeScreen() {
        lifecycleScope.launch {
            delay(2000)
            if (checkIfLoggedIn()) {
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))

            } else
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }
}