package com.yarmouk.bainah.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Message (
    var message:String="",
    var title:String=""
):Parcelable