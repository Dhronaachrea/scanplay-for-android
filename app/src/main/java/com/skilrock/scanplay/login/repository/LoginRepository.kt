package com.skilrock.scanplay.login.repository

import com.skilrock.scanplay.login.model.request.LoginRequest
import com.skilrock.scanplay.login.model.response.GetLoginDataResponse
import com.skilrock.scanplay.login.model.response.LoginResponse
import com.skilrock.scanplay.network.RetrofitNetworking
import retrofit2.Response

class LoginRepository {
    suspend fun callLoginApi(loginRequest: LoginRequest): Response<LoginResponse> {
        return RetrofitNetworking.create().callLoginApi(username = loginRequest.username, password = loginRequest.password, terminalId = loginRequest.terminalId, modelCode = loginRequest.modelCode)
    }

    suspend fun callGetLoginDataApi(): Response<GetLoginDataResponse> {
        return RetrofitNetworking.create().callGetLoginDataApi()
    }
}