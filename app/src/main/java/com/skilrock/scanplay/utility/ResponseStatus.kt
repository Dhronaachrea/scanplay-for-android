package com.skilrock.scanplay.utility

sealed class ResponseStatus<out T> {
    data class Success<T>(val response: T) : ResponseStatus<T>()
    data class Error(val responseMessage: String): ResponseStatus<Nothing>()
    data class TechnicalError(val responseMessage: String): ResponseStatus<Nothing>()
}
