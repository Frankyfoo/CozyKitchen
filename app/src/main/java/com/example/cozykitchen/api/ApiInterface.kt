package com.example.cozykitchen.api

import com.example.cozykitchen.model.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val BASE_URL = "http://kitchenapi-dev.ap-southeast-1.elasticbeanstalk.com/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ApiInterface {
    @GET("api/user")
    fun getUsers(): Call<List<User>>
}

object KitchenApi {
    val retrofitService : ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}