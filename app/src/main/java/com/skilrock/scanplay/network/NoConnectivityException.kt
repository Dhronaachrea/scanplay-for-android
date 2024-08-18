package com.skilrock.scanplay.network

import java.io.IOException

class NoConnectivityException : IOException() {

    override fun getLocalizedMessage(): String {
        return "NO INTERNET CONNECTIVITY"
    }

}