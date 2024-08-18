package com.skilrock.scanplay.home.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skilrock.scanplay.R
import com.skilrock.scanplay.base.viewmodel.BaseViewModel
import com.skilrock.scanplay.home.model.request.ProvideCouponRequest
import com.skilrock.scanplay.home.model.request.UpdateQrWithdrawalRequest
import com.skilrock.scanplay.home.model.response.PendingWithdrawalResponse
import com.skilrock.scanplay.home.model.response.ProvideCouponResponse
import com.skilrock.scanplay.home.model.response.UpdateQrWithdrawalResponse
import com.skilrock.scanplay.home.repository.DepositRepository
import com.skilrock.scanplay.home.repository.WithdrawalRepository
import com.skilrock.scanplay.network.ErrorResponse
import com.skilrock.scanplay.network.NoConnectivityException
import com.skilrock.scanplay.utility.APP_LOG_TAG
import com.skilrock.scanplay.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class HomeViewModel : BaseViewModel() {
    val liveDataDeposit             = MutableLiveData<ResponseStatus<ProvideCouponResponse>>()
    val liveDataPendingWithdrawal   = MutableLiveData<ResponseStatus<PendingWithdrawalResponse>>()
    val liveDataUpdateQrWithdrawal  = MutableLiveData<ResponseStatus<UpdateQrWithdrawalResponse>>()

    @SuppressLint("SuspiciousIndentation")
    fun callDepositViewModel(context: Context, provideCouponRequest: ProvideCouponRequest) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
              val depositResponse = DepositRepository().callProvideCouponApi(provideCouponRequest = provideCouponRequest)
                withContext(Dispatchers.Main) {
                    onDepositResponse(context, depositResponse)
                }
            } catch (e: Exception) {
                throw NoConnectivityException()
            }
        }
    }

    private fun onDepositResponse(context: Context, depositResponse: Response<ProvideCouponResponse>) {
        if (depositResponse.isSuccessful && depositResponse.body() != null) {
            val depositResponseBody = depositResponse.body()
            depositResponseBody?.let { response ->
                if (response.errorCode == 0)
                    liveDataDeposit.postValue(ResponseStatus.Success(response))
                else {
                    if (response.errorCode == 12429) {
                        liveDataDeposit.postValue(ResponseStatus.Error(responseMessage = "Session Expired, Please login" ?: context.getString(R.string.unable_to_fetch_information)))

                    } else {
                        liveDataDeposit.postValue(ResponseStatus.Error(responseMessage = response.errorMessage ?: context.getString(R.string.unable_to_fetch_information)))
                    }
                }

            } ?: liveDataDeposit.postValue(ResponseStatus.Error(responseMessage = context.getString(R.string.unable_to_fetch_information)))

        } else {
            liveDataDeposit.postValue(
                ResponseStatus.TechnicalError(responseMessage = context.getString(
                    R.string.some_technical_issue_occurred)))
        }
    }

    fun callUpdateQrWithdrawalViewModel(context: Context, updateQrWithdrawalRequest: UpdateQrWithdrawalRequest) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val updateQrWithdrawalResponse = WithdrawalRepository().callUpdateQrWithdrawalApi(updateQrWithdrawalRequest)
                withContext(Dispatchers.Main) {
                    onUpdateQrWithdrawalResponse(context, updateQrWithdrawalResponse)
                }
            } catch (e: Exception) {
                throw NoConnectivityException()
            }
        }
    }

    private fun onUpdateQrWithdrawalResponse(context: Context, updateQrWithdrawalResponse: Response<UpdateQrWithdrawalResponse>) {
        if (updateQrWithdrawalResponse.isSuccessful && updateQrWithdrawalResponse.body() != null) {
            val depositResponseBody = updateQrWithdrawalResponse.body()
            depositResponseBody?.let { response ->
                if (response.errorCode == 0)
                    liveDataUpdateQrWithdrawal.postValue(ResponseStatus.Success(response))
                else
                    liveDataUpdateQrWithdrawal.postValue(ResponseStatus.Error(responseMessage = response.errorMsg ?: context.getString(R.string.unable_to_fetch_information)))

            } ?: liveDataUpdateQrWithdrawal.postValue(ResponseStatus.Error(responseMessage = context.getString(R.string.unable_to_fetch_information)))

        } else {
            liveDataUpdateQrWithdrawal.postValue(
                ResponseStatus.TechnicalError(responseMessage = context.getString(
                    R.string.some_technical_issue_occurred)))
        }
    }

    fun callPendingWithdrawalViewModel(context: Context, userName: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val pendingWithdrawalResponse = WithdrawalRepository().callPendingWithdrawalApi(userName)
                withContext(Dispatchers.Main) {
                    onPendingWithdrawalResponse(context, pendingWithdrawalResponse)
                }

            } catch (e: Exception) {
                throw NoConnectivityException()
            }
        }
    }

    private fun onPendingWithdrawalResponse(context: Context, pendingWithdrawalResponse: Response<PendingWithdrawalResponse>) {
        if (pendingWithdrawalResponse.isSuccessful && pendingWithdrawalResponse.body() != null) {
            val depositResponseBody = pendingWithdrawalResponse.body()
            depositResponseBody?.let { response ->
                if (response.errorCode == 0)
                    liveDataPendingWithdrawal.postValue(ResponseStatus.Success(response))
                else
                    liveDataPendingWithdrawal.postValue(ResponseStatus.Error(responseMessage = response.errorMsg ?: context.getString(R.string.unable_to_fetch_information)))

            } ?: liveDataPendingWithdrawal.postValue(ResponseStatus.Error(responseMessage = context.getString(R.string.unable_to_fetch_information)))

        } else {
            liveDataPendingWithdrawal.postValue(
                ResponseStatus.TechnicalError(responseMessage = context.getString(
                    R.string.some_technical_issue_occurred)))
        }
    }
}