package com.skilrock.scanplay.network

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ErrorResponseInterceptor : Interceptor {
    private lateinit var jsonString: String

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val code = response.code()

        if (code in 200..300) {
            val bodyString = response.body()

            response.newBuilder().body(bodyString).build().body().let {
                val body = response.body()?.string()
                body?.let {
                    val element = JsonParser().parse(body)
                    val jsonObj: JsonObject = element.asJsonObject
                    Log.i("Okhttp", "$jsonObj")
                    jsonObj["responseData"].let { jsonElement ->
                        val jsonObj2: JsonObject = jsonElement.asJsonObject
                        if (jsonObj2.asJsonObject["statusCode"].toString() != "0") {
                            jsonString = jsonObj2.asJsonObject["message"].toString()
                            throw ErrorResponse(errorMsg = jsonString)
                        }
                    }
                }
            }
        }
        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}