package com.skilrock.scanplay.home.model.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateQrWithdrawalRequest(
    @SerializedName("aliasId")
    @Expose
    val aliasId: Int?,
    @SerializedName("amount")
    @Expose
    val amount: Double?,
    @SerializedName("domainId")
    @Expose
    val domainId: Int?,

    @SerializedName("requestId")
    @Expose
    val requestId: Int?,

    @SerializedName("userId")
    @Expose
    val userId: Int?,

    @SerializedName("appType")
    @Expose
    val appType: String? = "Android_Mobile"
,
    @SerializedName("retailerId")
    @Expose
    val retailerId: Int?,

    @SerializedName("device")
    @Expose
    val device: String? = "TERMINAL"




)