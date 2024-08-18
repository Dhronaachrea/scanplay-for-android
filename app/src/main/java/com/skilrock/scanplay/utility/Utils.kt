package com.skilrock.scanplay.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import java.security.MessageDigest

fun shakeError(): TranslateAnimation {
    val shake = TranslateAnimation(0f, 10f, 0f, 0f)
    shake.duration = 500
    shake.interpolator = CycleInterpolator(7f)
    return shake
}

fun vibrate(context: Context) {
    val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)
    vibrator?.let {
        if (Build.VERSION.SDK_INT >= 26) {
            it.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    this.requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun AppCompatEditText.getTrimText() : String {
    return text.toString().trim()
}

infix fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.visibilityGone() {
    visibility = View.GONE
}

/*fun View.visibilityInvisible() {
    visibility = View.INVISIBLE
}*/

fun View.visibilityVisible() {
    visibility = View.VISIBLE
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun isConnected(): Boolean {
    val connectivityManager = MyApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.run {
        getNetworkCapabilities(activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI) || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }
    } ?: false

    /* Below internet IN/OUT check code kept for future implementation */

    /*val mConnectivityManager = MyApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var mIsConnectionAvailable = false
    mConnectivityManager.run {
        registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                //take action when network connection is gained
                mIsConnectionAvailable = true
                Log.d(APP_LOG_TAG, "onAvailable: ")
            }

            override fun onLost(network: Network) {
                //take action when network connection is lost
                mIsConnectionAvailable = false
                Log.d(APP_LOG_TAG, "onLost: ")
            }
        })
        return mIsConnectionAvailable
    }*/
}

fun TextInputLayout.putError(errorMsg: String) {
    error = errorMsg
    requestFocus()
    startAnimation(shakeError())
}

fun TextInputLayout.removeError() {
    error = null
    requestFocus()
}

fun md5(s: String): String {
    val technique = "MD5"
    try {
        // Create MD5 Hash
        val digest = MessageDigest
            .getInstance(technique)
        digest.update(s.toByteArray())
        val messageDigest = digest.digest()

        // Create Hex String
        val hexString = StringBuilder()
        for (aMessageDigest in messageDigest) {
            var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
            while (h.length < 2) h = "0$h"
            hexString.append(h)
        }
        return hexString.toString()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun performLogoutCleanUp(activity: Activity, intent: Intent) {
    RetailerInfo.destroy()
    SharedPrefUtils.clearAppSharedPref(activity)
    activity.finish()
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
}

fun getDeviceModelCode(): String {
    return "NA"
}

@SuppressLint("PrivateApi")
fun getDeviceSerialNumber(): String? {
    var serialNumber: String?
    try {
        val c = Class.forName("android.os.SystemProperties")
        val get = c.getMethod("get", String::class.java)
        serialNumber = get.invoke(c, "gsm.sn1") as String
        if (serialNumber == "") serialNumber = get.invoke(c, "ril.serialnumber") as String
        if (serialNumber == "") serialNumber = get.invoke(c, "ro.serialno") as String
        if (serialNumber == "") serialNumber = get.invoke(c, "sys.serialnumber") as String

        // If none of the methods above worked
        if (serialNumber == "") serialNumber = null
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        serialNumber = null
    }
    try {
        Log.i("log", "Device Serial Number: $serialNumber")
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    if (serialNumber != null && serialNumber.equals("unknown", ignoreCase = true)) serialNumber =
        "NA"
    return serialNumber
}
