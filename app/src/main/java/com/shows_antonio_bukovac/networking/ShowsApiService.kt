package com.shows_antonio_bukovac.networking

import com.shows_antonio_bukovac.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ShowsApiService {

    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/users/sign_in")
    fun signIn(@Body request: LoginRequest): Call<LoginResponse>

    @GET("/shows")
    fun getShows(): Call<ShowsResponse>

    @GET("/shows/{id}/reviews")
    fun getReviews(@Path("id") id: Int): Call<ReviewsResponse>

    @GET("/shows/{id}")
    fun getShowDetails(@Path("id") id: Int): Call<ShowDetailsResponse>

    @POST("/reviews")
    fun addReview(@Body request: ReviewRequest): Call<ReviewResponse>

    @Multipart
    @PUT("/users")
    fun addImage(@Part image : MultipartBody.Part): Call<LoginResponse>
}
