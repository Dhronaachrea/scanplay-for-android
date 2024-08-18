package com.skilrock.scanplay.home.model.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateQrWithdrawalResponse(
    @SerializedName("data")
    @Expose
    val `data`: Data?,
    @SerializedName("errorCode")
    @Expose
    val errorCode: Int?,
    @SerializedName("errorMsg")
    @Expose
    val errorMsg: String?
) {
    data class Data(
        @SerializedName("aliasId")
        @Expose
        val aliasId: Int?,
        @SerializedName("amount")
        @Expose
        val amount: Double?,
        @SerializedName("batchId")
        @Expose
        val batchId: Int?,
        @SerializedName("cancelReason")
        @Expose
        val cancelReason: Any?,
        @SerializedName("cancelledBy")
        @Expose
        val cancelledBy: Int?,
        @SerializedName("createdAt")
        @Expose
        val createdAt: Long?,
        @SerializedName("device")
        @Expose
        val device: String?,
        @SerializedName("domainId")
        @Expose
        val domainId: Int?,
        @SerializedName("exchangeCharges")
        @Expose
        val exchangeCharges: Double?,
        @SerializedName("finalApprovalAt")
        @Expose
        val finalApprovalAt: Long?,
        @SerializedName("finalApprover")
        @Expose
        val finalApprover: Int?,
        @SerializedName("finalApproverDocument")
        @Expose
        val finalApproverDocument: Any?,
        @SerializedName("finalApproverRemark")
        @Expose
        val finalApproverRemark: String?,
        @SerializedName("firstApprovalAt")
        @Expose
        val firstApprovalAt: Long?,
        @SerializedName("firstApprover")
        @Expose
        val firstApprover: Int?,
        @SerializedName("firstApproverDocument")
        @Expose
        val firstApproverDocument: Any?,
        @SerializedName("firstApproverRemark")
        @Expose
        val firstApproverRemark: String?,
        @SerializedName("merchantChargesTxnId")
        @Expose
        val merchantChargesTxnId: Int?,
        @SerializedName("merchantId")
        @Expose
        val merchantId: Int?,
        @SerializedName("merchantTxnId")
        @Expose
        val merchantTxnId: Int?,
        @SerializedName("netAmount")
        @Expose
        val netAmount: Double?,
        @SerializedName("otpExpiry")
        @Expose
        val otpExpiry: Long?,
        @SerializedName("otpVerified")
        @Expose
        val otpVerified: String?,
        @SerializedName("paidBy")
        @Expose
        val paidBy: Int?,
        @SerializedName("paymentAccId")
        @Expose
        val paymentAccId: Int?,
        @SerializedName("paymentTypeId")
        @Expose
        val paymentTypeId: Int?,
        @SerializedName("podmId")
        @Expose
        val podmId: Int?,
        @SerializedName("processCharges")
        @Expose
        val processCharges: Double?,
        @SerializedName("providerId")
        @Expose
        val providerId: Int?,
        @SerializedName("providerTxnId")
        @Expose
        val providerTxnId: String?,
        @SerializedName("requestId")
        @Expose
        val requestId: Int?,
        @SerializedName("requestedBy")
        @Expose
        val requestedBy: Int?,
        @SerializedName("resendCount")
        @Expose
        val resendCount: Int?,
        @SerializedName("retryCount")
        @Expose
        val retryCount: Int?,
        @SerializedName("secondApprovalAt")
        @Expose
        val secondApprovalAt: Long?,
        @SerializedName("secondApprover")
        @Expose
        val secondApprover: Int?,
        @SerializedName("secondApproverDocument")
        @Expose
        val secondApproverDocument: Any?,
        @SerializedName("secondApproverRemark")
        @Expose
        val secondApproverRemark: String?,
        @SerializedName("status")
        @Expose
        val status: String?,
        @SerializedName("subTypeId")
        @Expose
        val subTypeId: Any?,
        @SerializedName("tpProviderTxnId")
        @Expose
        val tpProviderTxnId: Int?,
        @SerializedName("updatedAt")
        @Expose
        val updatedAt: Long?,
        @SerializedName("userId")
        @Expose
        val userId: Int?,
        @SerializedName("userTxnId")
        @Expose
        val userTxnId: Int?,
        @SerializedName("userType")
        @Expose
        val userType: String?,
        @SerializedName("verificationCode")
        @Expose
        val verificationCode: String?,
        @SerializedName("verifiedAt")
        @Expose
        val verifiedAt: Any?
    )
}