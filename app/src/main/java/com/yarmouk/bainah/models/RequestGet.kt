package com.yarmouk.bainah.models

import com.google.gson.annotations.SerializedName
import com.yarmouk.bainah.models.Participant

data class RequestGet(
    @SerializedName("_id")
    var requestId: String="",
    var fullName:String="",
    var licenseNumber:String="",
    var dateOfBirth:String="",
    var idNumber:String="",
    var phoneNumber:String="",
    var gender:String="",
    var driverLicense:String="",
    var carLicense:String="",
    var numberOfCars:Int=-1,
    var areaName:String="",
    var streetName:String="",
    var accidentImages:List<String> = emptyList(),
    var participants:List<Participant> = emptyList()

)
