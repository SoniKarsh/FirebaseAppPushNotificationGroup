package com.example.karshsoni.firebaseapppushnotification

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface FCM {

    @POST("send")
    fun send(
            @HeaderMap headers: Map<String, String>,
            @Body message: FCMResponse
    ): Call<ResponseBody>

}