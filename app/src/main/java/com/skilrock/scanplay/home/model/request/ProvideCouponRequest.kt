package com.skilrock.scanplay.home.model.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProvideCouponRequest(

    @SerializedName("aliasName")
    @Expose
    val aliasName: String?,

    @SerializedName("amount")
    @Expose
    val amount: Int?,

    @SerializedName("couponCount")
    @Expose
    val couponCount: Int?,

    @SerializedName("gameCode")
    @Expose
    val gameCode: String?,

    @SerializedName("providerCode")
    @Expose
    val providerCode: String?,

    @SerializedName("responseType")
    @Expose
    val responseType: String?,

    @SerializedName("retailerName")
    @Expose
    val retailerName: String?,

    @SerializedName("serviceCode")
    @Expose
    val serviceCode: String?,

    @SerializedName("deviceType")
    @Expose
    val deviceType: String? = "TERMINAL",

    @SerializedName("appType")
    @Expose
    val appType: String? = "Android_Mobile"
)