package com.skilrock.scanplay.base.modal


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponseCodeData(

    @SerializedName("code")
    @Expose
    val code: List<String>
)