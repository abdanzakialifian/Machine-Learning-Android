package com.learn.machinelearningandroid.data.remote.response

import com.google.gson.annotations.SerializedName

data class ImageClassificationResponse(
	@field:SerializedName("data")
	val data: ImageClassificationDataResponse? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ImageClassificationDataResponse(
	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("confidenceScore")
	val confidenceScore: Any? = null,

	@field:SerializedName("isAboveThreshold")
	val isAboveThreshold: Boolean? = null,

	@field:SerializedName("id")
	val id: String? = null
)
