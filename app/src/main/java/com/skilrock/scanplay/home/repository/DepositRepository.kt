package com.skilrock.scanplay.home.repository

import com.skilrock.scanplay.home.model.request.ProvideCouponRequest
import com.skilrock.scanplay.home.model.response.ProvideCouponResponse
import com.skilrock.scanplay.network.RetrofitNetworking
import com.skilrock.scanplay.utility.RetailerInfo
import retrofit2.Response

class DepositRepository {
    suspend fun callProvideCouponApi(provideCouponRequest: ProvideCouponRequest): Response<ProvideCouponResponse> {
        return RetrofitNetworking.create().callProvideCouponApi(playerToken = "Bearer " + RetailerInfo.getUserToken(), userId = RetailerInfo.getLoginDataUserId(), provideCouponRequest = provideCouponRequest)
    }
}