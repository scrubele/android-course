package com.scrubele.scrubeleapp1.retrofit

import com.scrubele.scrubeleapp1.models.ProtectedObjectModel
import com.scrubele.scrubeleapp1.models.RobotModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiInterface {

    @GET("protected_objects/")
    fun getProtectedObjects(): Call<List<ProtectedObjectModel>>

    @GET("robots")
    fun getRobots(): Call<List<RobotModel>>


    @Multipart
    @POST("protected_objects/")
    fun addProtectedObject(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("size") size: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<ProtectedObjectModel>
}