package com.skilrock.scanplay.home.repository

import com.skilrock.scanplay.home.model.request.UpdateQrWithdrawalRequest
import com.skilrock.scanplay.home.model.response.PendingWithdrawalResponse
import com.skilrock.scanplay.home.model.response.UpdateQrWithdrawalResponse
import com.skilrock.scanplay.network.RetrofitNetworking
import com.skilrock.scanplay.utility.RetailerInfo
import retrofit2.Response

class WithdrawalRepository {
    suspend fun callPendingWithdrawalApi(userName: String): Response<PendingWithdrawalResponse> {
        return RetrofitNetworking.create().callPendingWithdrawalApi(userName = userName, playerToken = "Bearer " + RetailerInfo.getUserToken(), userId = RetailerInfo.getLoginDataUserId())
    }

    suspend fun callUpdateQrWithdrawalApi(updateQrWithdrawalRequest: UpdateQrWithdrawalRequest): Response<UpdateQrWithdrawalResponse> {
        return RetrofitNetworking.create().callUpdateQrWithdrawalAPi(playerToken = "Bearer " + RetailerInfo.getUserToken(), userId = RetailerInfo.getLoginDataUserId(), updateQrWithdrawalRequest)
    }
}