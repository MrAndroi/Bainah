package com.yarmouk.bainah.models

data class Request(
    var fullName:String="",
    var licenseNumber:String="",
    var dateOfBirth:String="",
    var idNumber:String="",
    var phoneNumber:String="",
    var gender:String="",
    var driverLicense:String="",
    var carLicense:String="",
    var latitude: Double=0.0,
    var longitude: Double=0.0,
    var numberOfCars:Int=-1,
    var areaName:String="",
    var streetName:String="",
    var accidentImages:List<String> = emptyList(),
    var participants:List<Participant> = emptyList()

)
