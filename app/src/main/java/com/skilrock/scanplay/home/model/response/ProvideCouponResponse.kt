package com.skilrock.scanplay.home.model.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProvideCouponResponse(

    @SerializedName("data")
    @Expose
    val `data`: Data?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int?,

    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String?
) {
    data class Data(

        @SerializedName("couponCode")
        @Expose
        val couponCode: List<CouponCode?>?,

        @SerializedName("couponQRCodeUrl")
        @Expose
        val couponQRCodeUrl: String?
    ) {
        data class CouponCode(

            @SerializedName("couponCode")
            @Expose
            val couponCode: String?,

            @SerializedName("expiryDate")
            @Expose
            val expiryDate: Any?
        )
    }
}