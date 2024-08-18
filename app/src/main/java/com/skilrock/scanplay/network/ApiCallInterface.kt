package com.skilrock.scanplay.network

import com.skilrock.scanplay.home.model.request.ProvideCouponRequest
import com.skilrock.scanplay.home.model.request.UpdateQrWithdrawalRequest
import com.skilrock.scanplay.home.model.response.PendingWithdrawalResponse
import com.skilrock.scanplay.home.model.response.ProvideCouponResponse
import com.skilrock.scanplay.home.model.response.UpdateQrWithdrawalResponse
import com.skilrock.scanplay.login.model.response.GetLoginDataResponse
import com.skilrock.scanplay.login.model.response.LoginResponse
import com.skilrock.scanplay.utility.*
import retrofit2.Response
import retrofit2.http.*

interface ApiCallInterface {

    @Headers(HEADER_MERCHANT_CODE, MERCHANT_PASSWORD)
    @GET(LOGIN_URL)
    suspend fun callLoginApi(
        @Header("username") username: String,
        @Header("password") password: String,
        @Header("clientId") clientId: String            = CLIENT_ID,
        @Header("clientSecret") clientSecret: String    = CLIENT_PWD,
        @Query("terminalId") terminalId: String,
        @Query("modelCode") modelCode: String
    ) : Response<LoginResponse>

    @Headers(HEADER_MERCHANT_CODE, MERCHANT_PASSWORD)
    @GET(GET_LOGIN_URL)
    suspend fun callGetLoginDataApi(
        @Header("Authorization") playerToken: String = "Bearer " + RetailerInfo.getUserToken(),
    ) : Response<GetLoginDataResponse>

    @Headers(HEADER_MERCHANT_CODE, MERCHANT_PASSWORD, MERCHANT_ID)
    @POST(PROVIDE_COUPON_URL)
    suspend fun callProvideCouponApi(
        @Header("Authorization") playerToken: String,
        @Header("userId") userId: Int,
        @Body provideCouponRequest: ProvideCouponRequest
    ) : Response<ProvideCouponResponse>

    @Headers(HEADER_MERCHANT_CODE, MERCHANT_PASSWORD, MERCHANT_ID)
    @GET(PENDING_WITHDRAWAL_URL)
    suspend fun callPendingWithdrawalApi(
        @Header("Authorization") playerToken: String,
        @Header("userId") userId: Int,
        @Query("userName") userName: String
    ) : Response<PendingWithdrawalResponse>

    @Headers(HEADER_MERCHANT_CODE, MERCHANT_PASSWORD, MERCHANT_ID)
    @POST(UPDATE_WITHDRAWAL_URL)
    suspend fun callUpdateQrWithdrawalAPi(
        @Header("Authorization") playerToken: String,
        @Header("userId") userId: Int,
        @Body updateQrWithdrawalRequest: UpdateQrWithdrawalRequest
    ) : Response<UpdateQrWithdrawalResponse>
}