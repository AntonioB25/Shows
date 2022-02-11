package com.shows_antonio_bukovac.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewRequest(
    @SerialName("rating") var rating: Int,
    @SerialName("comment") var comment: String,
    @SerialName("show_id") var showId: Int
)