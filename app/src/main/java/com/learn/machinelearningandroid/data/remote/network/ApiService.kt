package com.learn.machinelearningandroid.data.remote.network

import com.learn.machinelearningandroid.data.remote.response.ImageClassificationResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("skin-cancer/predict")
    fun uploadImageClassification(
        @Part file: MultipartBody.Part,
    ): Call<ImageClassificationResponse>
}