package com.permata.mystoryyapp

import com.permata.mystoryyapp.network.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = i.toString(),
                name = "Author $i",
                description = "Description for story $i",
                photoUrl = "https://dummyurl.com/photo$i.jpg",
                lat = i.toDouble(),
                lon = i.toDouble(),
                createdAt = "2024-01-01T12:00:00Z"
            )
            items.add(story)
        }
        return items
    }
}
