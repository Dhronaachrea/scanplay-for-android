package com.skilrock.scanplay.login.viewmodel

import android.accounts.NetworkErrorException
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.scanplay.R
import com.skilrock.scanplay.base.viewmodel.BaseViewModel
import com.skilrock.scanplay.login.model.request.LoginRequest
import com.skilrock.scanplay.login.model.response.GetLoginDataResponse
import com.skilrock.scanplay.login.model.response.LoginResponse
import com.skilrock.scanplay.login.repository.LoginRepository
import com.skilrock.scanplay.network.NoConnectivityException
import com.skilrock.scanplay.utility.APP_LOG_TAG
import com.skilrock.scanplay.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class LoginViewModel : BaseViewModel() {
    val liveDataLogin = MutableLiveData<ResponseStatus<LoginResponse>>()
    val liveDataGetLoginData = MutableLiveData<ResponseStatus<GetLoginDataResponse>>()

    fun callLoginApi(context: Context, loginRequest: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val loginResponse = LoginRepository().callLoginApi(loginRequest = loginRequest)
                withContext(Dispatchers.Main) {
                    onLoginResponse(context, loginResponse)
                }
            } catch (e:Exception) {
                throw NoConnectivityException()
            }
        }
    }

    private fun onLoginResponse(context: Context, loginResponse: Response<LoginResponse>) {
        if (loginResponse.isSuccessful && loginResponse.body() != null) {
            val loginResponseBody = loginResponse.body()
            loginResponseBody?.let { response ->
                if (response.responseData?.statusCode == 0)
                    liveDataLogin.postValue(ResponseStatus.Success(response))
                else
                    liveDataLogin.postValue(ResponseStatus.Error(responseMessage = response.responseData?.message ?: context.getString(R.string.unable_to_fetch_information)))

            } ?: liveDataLogin.postValue(ResponseStatus.Error(responseMessage = context.getString(R.string.unable_to_fetch_information)))

        } else {
            liveDataLogin.postValue(ResponseStatus.TechnicalError(responseMessage = context.getString(R.string.some_technical_issue_occurred)))
        }
    }

    fun callGetLoginData(context: Context) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val getLoginDataResponse = LoginRepository().callGetLoginDataApi()
            withContext(Dispatchers.Main) {
                onGetLoginDataResponse(context, getLoginDataResponse)
            }
        }
    }

    private fun onGetLoginDataResponse(context: Context, loginResponse: Response<GetLoginDataResponse>) {
        if (loginResponse.isSuccessful && loginResponse.body() != null) {
            val loginResponseBody = loginResponse.body()
            loginResponseBody?.let { response ->
                if (response.responseData?.statusCode == 0)
                    liveDataGetLoginData.postValue(ResponseStatus.Success(response))
                else
                    liveDataGetLoginData.postValue(ResponseStatus.Error(responseMessage = response.responseData?.message ?: context.getString(R.string.unable_to_fetch_information)))

            } ?: liveDataGetLoginData.postValue(ResponseStatus.Error(responseMessage = context.getString(R.string.unable_to_fetch_information)))

        } else {
            liveDataGetLoginData.postValue(ResponseStatus.TechnicalError(responseMessage = context.getString(R.string.some_technical_issue_occurred)))
        }
    }
}

