package com.skilrock.scanplay.utility

sealed class ResponseStatusError {
    data class NoInternetError(val errorMessageCode: Int) : ResponseStatusError()
    data class TechnicalError(val errorMessageCode: Int) : ResponseStatusError()
    data class ErrorResponseError(val errorMessage: String) : ResponseStatusError()
}
