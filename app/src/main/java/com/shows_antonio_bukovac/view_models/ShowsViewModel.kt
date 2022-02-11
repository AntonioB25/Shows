package com.shows_antonio_bukovac.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shows_antonio_bukovac.model.Show
import com.shows_antonio_bukovac.model.ShowsResponse
import com.shows_antonio_bukovac.networking.ApiModule
import retrofit2.Call
import retrofit2.Response

class ShowsViewModel : ViewModel() {

    private val showsLiveData: MutableLiveData<List<Show>> by lazy {
        MutableLiveData<List<Show>>()
    }

    fun getShowsLiveData(): LiveData<List<Show>> {
        getShows()
        return showsLiveData
    }

    private fun getShows() {
        ApiModule.retrofit.getShows().enqueue(object :
            retrofit2.Callback<ShowsResponse> {

            override fun onResponse(call: Call<ShowsResponse>, response: Response<ShowsResponse>) {
                showsLiveData.value = response.body()?.shows
            }

            override fun onFailure(call: Call<ShowsResponse>, t: Throwable) {
                Log.e("Show failure",t.message.toString())
                showsLiveData.value = emptyList()
            }
        })
    }
}