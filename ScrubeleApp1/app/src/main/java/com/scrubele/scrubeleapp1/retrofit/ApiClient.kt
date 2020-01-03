package com.scrubele.scrubeleapp1.retrofit

import android.app.Application
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient : Application() {

    //    private var BASE_URL: String = "https://wired-sol-231410.appspot.com/api/"
    private var BASE_URL: String = "http://104.154.190.119:8000/api/"
    val getClient: ApiInterface
        get() {
            val jsonBuilder = GsonBuilder()
                .setLenient()
                .create()
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(jsonBuilder))
                .build()
            return retrofit.create(ApiInterface::class.java)
        }

}