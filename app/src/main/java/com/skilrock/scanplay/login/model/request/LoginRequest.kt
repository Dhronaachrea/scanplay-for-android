package com.skilrock.scanplay.login.model.request

data class LoginRequest(
    val username: String,
    val password: String,
    val terminalId: String,
    val modelCode: String,
)
