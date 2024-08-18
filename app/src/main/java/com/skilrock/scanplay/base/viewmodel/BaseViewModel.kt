package com.skilrock.scanplay.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skilrock.scanplay.R
import com.skilrock.scanplay.network.ErrorResponse
import com.skilrock.scanplay.network.NoConnectivityException
import com.skilrock.scanplay.utility.ResponseStatusError
import kotlinx.coroutines.CoroutineExceptionHandler

/**
 * Created by Rajneesh Kumar Sharma on 27 Jan 2023.
 * rajneeshk.sharma@skilrock.com
 * Skilrock Technologies
 */

abstract class BaseViewModel: ViewModel() {
    val liveDataNetworkError        = MutableLiveData<ResponseStatusError>()
    //val liveDataErrorResponseError  = MutableLiveData<ResponseStatusError>() // Kept for Error Response Interceptor for future implementation

    val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is NoConnectivityException  -> liveDataNetworkError.postValue(ResponseStatusError.NoInternetError(R.string.check_internet_connection))
            //is ErrorResponse            -> liveDataErrorResponseError.postValue(ResponseStatusError.ErrorResponseError(throwable.localizedMessage)) // Kept for Error Response Interceptor for future implementation
        }
    }
}