package com.yarmouk.bainah.models

data class ImagesResponse(
    val imagesPaths:List<String> = emptyList(),
    val message:String,
    val status:Boolean
)
