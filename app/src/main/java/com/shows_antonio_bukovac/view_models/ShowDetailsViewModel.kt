package com.shows_antonio_bukovac.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shows_antonio_bukovac.model.*
import com.shows_antonio_bukovac.networking.ApiModule
import retrofit2.Call
import retrofit2.Response

class ShowDetailsViewModel : ViewModel() {

    private val showDetailsLiveData: MutableLiveData<Show> by lazy {
        MutableLiveData<Show>()
    }

    fun getShowDetailsLiveData(showId: Int): LiveData<Show> {
        getShowDetails(showId)
        return showDetailsLiveData
    }

    private fun getShowDetails(showId: Int){
        ApiModule.retrofit.getShowDetails(showId).enqueue(object :
            retrofit2.Callback<ShowDetailsResponse> {
            override fun onResponse(
                call: Call<ShowDetailsResponse>,
                response: Response<ShowDetailsResponse>
            ) {
                showDetailsLiveData.value = response.body()?.show
            }

            override fun onFailure(call: Call<ShowDetailsResponse>, t: Throwable) {
                showDetailsLiveData.value = null
            }
        })
    }
}