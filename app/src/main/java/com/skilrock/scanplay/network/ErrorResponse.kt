package com.skilrock.scanplay.network

import java.io.IOException

class ErrorResponse(private val errorMsg : String) : IOException() {
    override fun getLocalizedMessage(): String {
        return errorMsg
    }
}