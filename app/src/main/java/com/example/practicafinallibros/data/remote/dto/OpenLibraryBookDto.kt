package com.example.practicafinallibros.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenLibraryBookDto(
    val key: String,
    val title: String,
    @SerialName("author_name") val authorName: List<String>? = null,
    @SerialName("first_publish_year") val firstPublishYear: Int? = null,
    @SerialName("cover_i") val coverId: Int? = null,
    @SerialName("number_of_pages_median") val numberOfPagesMedian: Int? = null,
    val subject: List<String>? = null
)
