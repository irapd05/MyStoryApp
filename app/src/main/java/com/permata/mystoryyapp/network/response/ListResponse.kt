package com.permata.mystoryyapp.network.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class ListResponse(

    @field:SerializedName("listStory")
	val listStory: List<ListStoryItem>,

    @field:SerializedName("error")
	val error: Boolean,

    @field:SerializedName("message")
	val message: String
)

@Entity(tableName = "list_story")
data class ListStoryItem(

	@PrimaryKey
	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("lon")
	val lon: Double,

	@field:SerializedName("lat")
	val lat: Double
)
