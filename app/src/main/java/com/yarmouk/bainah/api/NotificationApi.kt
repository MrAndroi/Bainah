package com.yarmouk.bainah.api


import com.yarmouk.bainah.models.Notification
import com.yarmouk.bainah.others.Constans.CONTENT_TYPE
import com.yarmouk.bainah.others.Constans.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: Notification
    ): Response<ResponseBody>

}