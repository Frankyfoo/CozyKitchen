package com.example.cozykitchen.helper

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

object RequestBodySingleton {
    private val gson = Gson()

    fun makeGSONRequestBody(jsonObject: Any?): RequestBody {
        return RequestBody.create(
            MediaType.parse("multipart/form-data"),
            gson.toJson(jsonObject)
        )
    }
}