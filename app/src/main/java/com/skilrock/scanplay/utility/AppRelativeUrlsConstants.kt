package com.skilrock.scanplay.utility

import com.skilrock.scanplay.BuildConfig.BASE_URL

/* Write your Relative Urls here */
const val CASHIER_BASE_URL      =  "https://cashier-backend-wls.infinitilotto.com"

const val LOGIN_URL             = "${BASE_URL}/RMS/get/token"
const val GET_LOGIN_URL             = "${BASE_URL}/RMS/v1.0/getLoginData"
const val PROVIDE_COUPON_URL    = "https://bonus-backend-wls.infinitilotto.com/rms/provideCoupon"
const val PENDING_WITHDRAWAL_URL= "${CASHIER_BASE_URL}/rms/pendingWithdrawalByQrcode"
const val UPDATE_WITHDRAWAL_URL = "${CASHIER_BASE_URL}/rms/updateQRWithdrawalRequest"
