package com.yarmouk.bainah.models

import androidx.annotation.Keep
import com.yarmouk.bainah.models.Message

@Keep
data class Notification (
    val data: Message,
    val to:String
)
