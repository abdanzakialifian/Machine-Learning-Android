package com.learn.machinelearningandroid.imageclassification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.learn.machinelearningandroid.data.remote.network.ApiConfig
import com.learn.machinelearningandroid.data.remote.response.ImageClassificationResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class ImageClassificationViewModel : ViewModel() {
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading get(): LiveData<Boolean> = _isLoading

    private val _resultMessage: MutableLiveData<String> = MutableLiveData("")
    val resultMessage get(): LiveData<String> = _resultMessage

    private val _aboveThresholdMessage: MutableLiveData<String> = MutableLiveData("")
    val aboveThresholdMessage get(): LiveData<String> = _aboveThresholdMessage

    private val _errorMessage: MutableLiveData<String> = MutableLiveData("")
    val errorMessage get(): LiveData<String> = _errorMessage
    fun uploadFile(imageFile: File) {
        _isLoading.postValue(true)

        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData(
            name = "photo",
            filename = imageFile.name,
            body = requestImageFile,
        )

        val apiService = ApiConfig.getApiService()
        apiService.uploadImageClassification(multipartBody)
            .enqueue(object : Callback<ImageClassificationResponse?> {
                override fun onResponse(
                    call: Call<ImageClassificationResponse?>,
                    response: Response<ImageClassificationResponse?>
                ) {
                    _isLoading.postValue(false)
                    try {
                        val responseBody = response.body()

                        if (response.isSuccessful.not()) {
                            _errorMessage.postValue(response.message())
                            return
                        }

                        if (responseBody?.data?.isAboveThreshold == true) {
                            _resultMessage.postValue(
                                String.format(
                                    "%s with %.2f%%",
                                    responseBody.data.result,
                                    responseBody.data.confidenceScore,
                                )
                            )
                            _aboveThresholdMessage.postValue(responseBody.message.toString())
                        } else {
                            _resultMessage.postValue(
                                String.format(
                                    "Please use the correct picture because  the confidence score is %.2f%%",
                                    responseBody?.data?.confidenceScore,
                                )
                            )

                            _aboveThresholdMessage.postValue(
                                "Model is predicted successfully but under threshold.",
                            )
                        }
                    } catch (e: HttpException) {
                        val errorBody = e.response()?.errorBody()?.toString()
                        val errorResponse =
                            Gson().fromJson(errorBody, ImageClassificationResponse::class.java)
                        _errorMessage.postValue(errorResponse.message.orEmpty())
                    }
                }

                override fun onFailure(call: Call<ImageClassificationResponse?>, t: Throwable) {
                    _isLoading.postValue(false)
                    _errorMessage.postValue(t.message.orEmpty())
                }
            })
    }
}