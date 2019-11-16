package com.scrubele.scrubeleapp1.retrofit

import com.scrubele.scrubeleapp1.models.ProtectedObjectModel
import com.scrubele.scrubeleapp1.models.RobotModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @GET("protected_objects")
    fun getProtectedObjects(): Call<List<ProtectedObjectModel>>

    @GET("robots")
    fun getRobots(): Call<List<RobotModel>>
}