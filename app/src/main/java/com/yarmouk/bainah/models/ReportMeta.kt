package com.yarmouk.bainah.models

import com.google.gson.annotations.SerializedName

data class ReportMeta(
    @SerializedName("_id")
    var reportId: String = "",
    var fullName: String = "",
    var driverLicense: String = "",
    var phoneNumber: String = "",
)