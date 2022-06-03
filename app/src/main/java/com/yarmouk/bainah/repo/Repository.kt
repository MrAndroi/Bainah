package com.yarmouk.bainah.repo

import com.yarmouk.bainah.api.BainahApi
import com.yarmouk.bainah.models.Participant
import com.yarmouk.bainah.models.Request
import javax.inject.Inject

class Repository @Inject constructor(private val api: BainahApi){

    suspend fun postNewRequest(request: Request) = api.postNewRequest(request)

    suspend fun addNewParticipant(requestId:String,participant: Participant)
    = api.addNewParticipant(requestId,participant)

    suspend fun getRequest(requestId:String) = api.getRequest(requestId)

    suspend fun getAllReports() = api.getAllReports()
}