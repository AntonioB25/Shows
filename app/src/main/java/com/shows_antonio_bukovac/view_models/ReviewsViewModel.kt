package com.shows_antonio_bukovac.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shows_antonio_bukovac.ReviewsAdapter
import com.shows_antonio_bukovac.model.Review
import com.shows_antonio_bukovac.model.ReviewRequest
import com.shows_antonio_bukovac.model.ReviewResponse
import com.shows_antonio_bukovac.model.ReviewsResponse
import com.shows_antonio_bukovac.networking.ApiModule
import retrofit2.Call
import retrofit2.Response

class ReviewsViewModel : ViewModel() {

    private val reviewsLiveData: MutableLiveData<List<Review>> by lazy {
        MutableLiveData<List<Review>>()
    }

    private val reviewLiveData:MutableLiveData<Review> by lazy {
        MutableLiveData<Review>()
    }

    /**
     * Get last added review
     */
    fun getReviewLiveData(): LiveData<Review>{
        return reviewLiveData
    }

    fun getReviewsLiveData(showId: Int): LiveData<List<Review>> {
        getReviews(showId)
        return reviewsLiveData
    }

    private fun getReviews(showId: Int) {
        ApiModule.retrofit.getReviews(showId).enqueue(object :
            retrofit2.Callback<ReviewsResponse> {
            override fun onResponse(
                call: Call<ReviewsResponse>,
                response: Response<ReviewsResponse>
            ) {
                reviewsLiveData.value = response.body()?.reviews
            }

            override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                reviewsLiveData.value = emptyList()
            }
        })
    }

    fun addReview(reviewRequest: ReviewRequest){

        ApiModule.retrofit.addReview(reviewRequest).enqueue(object :
            retrofit2.Callback<ReviewResponse> {
            override fun onResponse(
                call: Call<ReviewResponse>,
                response: Response<ReviewResponse>
            ) {
                reviewLiveData.value = response.body()?.review
            }

            override fun onFailure(call: Call<ReviewResponse>, t: Throwable) {

            }
        })
    }
}

