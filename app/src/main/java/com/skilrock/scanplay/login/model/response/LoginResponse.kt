package com.skilrock.scanplay.login.model.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("responseCode")
    @Expose
    val responseCode: Int?,

    @SerializedName("responseData")
    @Expose
    val responseData: ResponseData?,

    @SerializedName("responseMessage")
    @Expose
    val responseMessage: String?
) {
    data class ResponseData(

        @SerializedName("authToken")
        @Expose
        val authToken: String?,

        @SerializedName("expiryTime")
        @Expose
        val expiryTime: String?,

        @SerializedName("forcePasswordUpdate")
        @Expose
        val forcePasswordUpdate: String?,

        @SerializedName("issueAt")
        @Expose
        val issueAt: String?,

        @SerializedName("message")
        @Expose
        val message: String?,

        @SerializedName("statusCode")
        @Expose
        val statusCode: Int?,

        @SerializedName("timestamp")
        @Expose
        val timestamp: String?,

        @SerializedName("userId")
        @Expose
        val userId: Int?
    )
}