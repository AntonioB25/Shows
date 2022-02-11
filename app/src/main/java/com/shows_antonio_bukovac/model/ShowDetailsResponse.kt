package com.shows_antonio_bukovac.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowDetailsResponse(
    @SerialName("show") val show: Show
)