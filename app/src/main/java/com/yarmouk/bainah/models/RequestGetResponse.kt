package com.yarmouk.bainah.models

data class RequestGetResponse(
    val success:Boolean,
    val message:String,
    val request: RequestGet
)
