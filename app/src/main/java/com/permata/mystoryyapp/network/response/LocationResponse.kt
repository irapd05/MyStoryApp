package com.permata.mystoryyapp.network.response

import com.google.gson.annotations.SerializedName

data class LocationResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("listStory")
	val listStory: List<StoryLocation>
)

data class StoryLocation(

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("lat")
	val latitude: Double,

	@field:SerializedName("lon")
	val longitude: Double,

	@field:SerializedName("description")
	val description: String
)
