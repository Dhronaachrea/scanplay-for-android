package com.skilrock.scanplay.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer

class CustomHttpLoggingInterceptor : Interceptor {

    private val mMaxLogLength = 4000

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        Log.d("OkHttp", java.lang.String.format("[%s] %s on %s%n", request.method(), request.url(), chain.connection()))
        Log.d("OkHttp", java.lang.String.format("Header: %s", request.headers()))
        val requestBuffer = Buffer()
        request.body()?.writeTo(requestBuffer)
        Log.v("OkHttp", "Request: --> " + requestBuffer.readUtf8())
        val response = chain.proceed(request)
        val contentType = response.body()?.contentType()
        val content = response.body()?.string()
        Log.i("OkHttp", "Response: <--  $content")
        log(content ?: "")
        val wrappedBody = ResponseBody.create(contentType, content ?: "")
        return response.newBuilder().body(wrappedBody).build()
    }

    private fun log(message: String) {
        var i = 0
        val length = message.length
        while (i < length) {
            var newline = message.indexOf('\n', i)
            newline = if (newline != -1) newline else length
            do {
                val end = newline.coerceAtMost(i + mMaxLogLength)
                //Log.i("OkHttp", message.substring(i, end))
                /*if (i == 0)
                    Log.i("OkHttp", "[Response for $url] -> ${message.substring(i, end)}")
                else
                    Log.i("OkHttp", message.substring(i, end))*/
                i = end
            } while (i < newline)
            i++
        }
    }
}