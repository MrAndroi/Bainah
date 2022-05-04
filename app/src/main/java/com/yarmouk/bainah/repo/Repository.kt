package com.yarmouk.bainah.repo

import com.yarmouk.bainah.api.BalaghApi
import com.yarmouk.bainah.api.NotificationApi
import com.yarmouk.bainah.models.Notification
import com.yarmouk.bainah.models.Participant
import com.yarmouk.bainah.models.Request
import okhttp3.MultipartBody
import javax.inject.Inject

class Repository @Inject constructor(private val api: BalaghApi, private val notificationApi: NotificationApi){

    suspend fun postNewRequest(request: Request) = api.postNewRequest(request)

    suspend fun addNewParticipant(requestId:String,participant: Participant)
    = api.addNewParticipant(requestId,participant)

    suspend fun getRequest(requestId:String) = api.getRequest(requestId)

    suspend fun postNotification(notification: Notification) = notificationApi.postNotification(notification)

}