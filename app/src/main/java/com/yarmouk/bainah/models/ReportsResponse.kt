package com.yarmouk.bainah.models

data class ReportsResponse(
    val results: List<ReportMeta>,
    val success:Boolean,
    val message:String
)