package com.example.cozykitchen.api

import com.example.cozykitchen.model.*
import com.example.cozykitchen.request.PostAddress
import com.example.cozykitchen.request.PostCard
import com.example.cozykitchen.request.PostOrder
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

    // User API
    @GET("api/user")
    fun getUsers(): Call<List<User>>

    @Headers("Content-Type: application/json")
    @POST("api/user")
    fun createUser(@Body requestBody: RequestBody): Call<User>

    // Shop API
    @GET("api/shop")
    fun getShops(): Call<List<Shop>>

    @Headers("Content-Type: application/json")
    @POST("api/shop")
    fun addShop(@Body requestBody: RequestBody, @Header("chefId") chefId: String): Call<Shop>

    // Product API
    @GET("api/product/shop/{id}")
    fun getFoodByShopId(@Path("id") id: String): Call<List<Product>>

    @GET("api/product/{id}")
    fun getFoodById(@Path("id") id: String): Call<Product>

    // ShoopingCart API
    @Headers("Content-Type: application/json")
    @POST("api/shoppingcart")
    fun addProductToCart(@Body requestBody: RequestBody): Call<ShoppingCart>

    @GET("api/shoppingcart/GetShoppingCartListByUserId/{userId}")
    fun getShoppingCartListByUserId(@Path("userId") userId: String): Call<List<ShoppingCart>>

    @GET("api/shoppingcart/getshoppingcartlistbyorderid/{orderId}")
    fun getShoppingCartListByOrderId(@Path("orderId") orderId: String): Call<List<ShoppingCart>>

    @DELETE("api/shoppingcart/{id}")
    fun deleteShoppingCart(@Path("id") id: String): Call<ShoppingCart>

    // Address API
    @Headers("Content-Type: application/json")
    @POST("api/address")
    fun addNewAddress(@Body requestBody: RequestBody): Call<PostAddress>

    @GET("api/address/getaddressesbyuserid/{userId}")
    fun getAddressesByUserId(@Path("userId") userId: String): Call<List<Address>>

    @GET("api/address/{id}")
    fun getAddressById(@Path("id") id: String): Call<Address>

    @Headers("Content-Type: application/json")
    @PUT("api/address/{id}")
    fun updateAddress(@Path("id") id: String, @Body requestBody: RequestBody): Call<Address>

    @DELETE("api/address/{id}")
    fun deleteAddress(@Path("id") id: String): Call<Address>

    // Card API
    @Headers("Content-Type: application/json")
    @POST("api/card")
    fun addNewCard(@Body requestBody: RequestBody): Call<PostCard>

    @GET("api/card/getcardsbyuserid/{userId}")
    fun getCardsByUserId(@Path("userId") userId: String): Call<List<Card>>

    @GET("api/card/{id}")
    fun getCardById(@Path("id") id: String): Call<Card>

    @Headers("Content-Type: application/json")
    @PUT("api/card/{id}")
    fun updateCard(@Path("id") id: String, @Body requestBody: RequestBody): Call<Card>

    @DELETE("api/card/{id}")
    fun deleteCard(@Path("id") id: String): Call<Card>

    // Order API
    @GET("api/order/{id}")
    fun getOrderById(@Path("id") id: String): Call<Order>

    @GET("api/order/OrdersbyUserId/{userId}")
    fun getOrdersByUserId(@Path("userId") userId: String): Call<List<Order>>

    @Headers("Content-Type: application/json")
    @POST("api/order")
    fun addOrder(@Body requestBody: RequestBody): Call<PostOrder>

    // Chef API
    @GET("api/chef")
    fun getChefs(): Call<List<Chef>>

    @GET("api/chef/{id}")
    fun getChefById(@Path("id") id: String): Call<Chef>
}

object KitchenApi {
    val retrofitService : ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}