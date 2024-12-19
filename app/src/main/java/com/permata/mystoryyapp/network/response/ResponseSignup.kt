package com.permata.mystoryyapp.network.response

import com.google.gson.annotations.SerializedName

data class ResponseSignup(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	val data: UserData? = null

)

data class UserData(
	val userId: String
)
