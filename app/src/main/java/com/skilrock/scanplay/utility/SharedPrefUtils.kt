package com.skilrock.scanplay.utility

import android.content.Context

object SharedPrefUtils {

    private const val USER_PREF         = "SCAN_PLAY"
    const val USER_TOKEN                = "userToken"
    const val USER_NAME                 = "userToken"
    const val USER_ID                   = "userId"
    const val LOGIN_DATA_USER_ID        = "loginDataUserId"
    const val USER_DATA                 = "userData"
    const val USER_PARTICULAR_DATA      = "userParticularData"

    fun getString(context: Context, key: String): String {
        return context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).getString(key, "") ?: ""
    }

    fun setString(context: Context, key: String, value: String) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().putString(key, value).apply()
    }

    /*fun getInt(context: Context, key: String): Int {
        return context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).getInt(key, -1)
    }*/

    fun setInt(context: Context, key: String, value: Int) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().putInt(key, value).apply()
    }

    fun clearAppSharedPref(context: Context) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().clear().apply()
    }

}