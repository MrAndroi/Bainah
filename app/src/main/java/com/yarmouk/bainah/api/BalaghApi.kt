package com.yarmouk.bainah.api

import com.yarmouk.bainah.models.Participant
import com.yarmouk.bainah.models.Request
import com.yarmouk.bainah.models.RequestGetResponse
import com.yarmouk.bainah.models.RequestResponse
import retrofit2.Response
import retrofit2.http.*

interface BalaghApi {

    @POST("requests/postRequeset")
    suspend fun postNewRequest(
        @Body request: Request
    ):Response<RequestResponse>

    @GET("requests/{requestId}")
    suspend fun getRequest(
        @Path("requestId") requestId:String
    ):Response<RequestGetResponse>

    @PUT("requests/{requestId}/addParticipant")
    suspend fun addNewParticipant(
        @Path("requestId") requestId:String,
        @Body participant: Participant
    ):Response<RequestResponse>
}