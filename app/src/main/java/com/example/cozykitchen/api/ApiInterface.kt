package com.example.cozykitchen.api

import com.example.cozykitchen.model.Product
import com.example.cozykitchen.model.Shop
import com.example.cozykitchen.model.ShoppingCart
import com.example.cozykitchen.model.User
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL = "http://kitchenapi-dev.ap-southeast-1.elasticbeanstalk.com/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ApiInterface {
    @GET("api/user")
    fun getUsers(): Call<List<User>>

    @Headers("Content-Type: application/json")
    @POST("api/user")
    fun createUser(@Body requestBody: RequestBody): Call<User>

    @GET("api/shop")
    fun getShops(): Call<List<Shop>>

    @GET("api/product/shop/{id}")
    fun getFoodByShopId(@Path("id") id: String): Call<List<Product>>

    @GET("api/product/{id}")
    fun getFoodById(@Path("id") id: String): Call<Product>

    @Headers("Content-Type: application/json")
    @POST("api/shoppingcart")
    fun addProductToCart(@Body requestBody: RequestBody): Call<ShoppingCart>
}

object KitchenApi {
    val retrofitService : ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}