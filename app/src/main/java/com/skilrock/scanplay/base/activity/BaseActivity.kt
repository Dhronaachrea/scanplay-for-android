package com.skilrock.scanplay.base.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.skilrock.scanplay.R
import com.skilrock.scanplay.login.model.response.GetLoginDataResponse
import com.skilrock.scanplay.login.model.response.LoginResponse
import com.skilrock.scanplay.utility.ResponseStatusError
import com.skilrock.scanplay.utility.RetailerInfo
import com.skilrock.scanplay.utility.SharedPrefUtils
import com.skilrock.scanplay.utility.showToast

/**
 * Created by Rajneesh Kumar Sharma on 27 Jan 2023.
 * rajneeshk.sharma@skilrock.com
 * Skilrock Technologies
 */

abstract class BaseActivity : AppCompatActivity() {

    open val observerError = Observer<ResponseStatusError> {
        when (it) {
            is ResponseStatusError.NoInternetError -> {
                showToast(getString(R.string.check_internet_connection))
            }

            is ResponseStatusError.TechnicalError -> {
                showToast(getString(R.string.some_technical_issue_occurred))
            }

            is ResponseStatusError.ErrorResponseError -> {}
        }
    }

    fun checkIfLoggedIn() : Boolean {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.USER_TOKEN).isEmpty()) {
            return false

        } else {
            try {
                RetailerInfo.setLoginData(Gson().fromJson(SharedPrefUtils.getString(this, SharedPrefUtils.USER_DATA), LoginResponse::class.java))
                RetailerInfo.setGetLoginData(Gson().fromJson(SharedPrefUtils.getString(this, SharedPrefUtils.USER_PARTICULAR_DATA), GetLoginDataResponse::class.java))
            } catch (_: Exception) {

            }

        }
        return true
    }
}