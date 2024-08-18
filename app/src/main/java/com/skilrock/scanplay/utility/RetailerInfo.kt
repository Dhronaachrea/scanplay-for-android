package com.skilrock.scanplay.utility

import android.content.Context
import com.google.gson.Gson
import com.skilrock.scanplay.login.model.response.GetLoginDataResponse
import com.skilrock.scanplay.login.model.response.LoginResponse

object RetailerInfo {
    private var loginData: LoginResponse? = null
    private var getLoginResponseData: GetLoginDataResponse? = null

    fun setLoginData(loginResponse: LoginResponse) {
        loginData = loginResponse
    }

    fun setGetLoginData(getLoginDataResponse: GetLoginDataResponse) {
        getLoginResponseData = getLoginDataResponse
    }

    /*fun getLoginData() : LoginResponse? {
        return loginData
    }

    fun getLoginResponseData() : GetLoginDataResponse? {
        return getLoginResponseData
    }*/

    fun destroy() {
        loginData = null
    }

    fun setLoginData(context: Context, loginResponse: LoginResponse) {
        loginData = loginResponse
        SharedPrefUtils.setInt(context, SharedPrefUtils.USER_ID, loginResponse.responseData?.userId ?: 0)
        SharedPrefUtils.setString(context, SharedPrefUtils.USER_TOKEN, loginResponse.responseData?.authToken ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.USER_DATA, Gson().toJson(loginResponse))
    }

    fun setGetLoginData(context: Context, getLoginDataResponse: GetLoginDataResponse) {
        getLoginResponseData = getLoginDataResponse
        SharedPrefUtils.setString(context, SharedPrefUtils.USER_NAME, getLoginDataResponse.responseData?.data?.username ?: "")
        SharedPrefUtils.setInt(context, SharedPrefUtils.LOGIN_DATA_USER_ID, getLoginDataResponse.responseData?.data?.userId ?: 0)
        SharedPrefUtils.setString(context, SharedPrefUtils.USER_PARTICULAR_DATA, Gson().toJson(getLoginDataResponse))
    }

    fun getUserToken() : String     =   loginData?.responseData?.authToken                  ?: ""
    fun getLoginDataUserId() : Int  =   getLoginResponseData?.responseData?.data?.userId    ?: 0
    fun getUsername() : String      =   getLoginResponseData?.responseData?.data?.username  ?: ""
    fun getOrgName() : String       =   getLoginResponseData?.responseData?.data?.orgName  ?: ""

}