package com.shows_antonio_bukovac.view_models

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.shows_antonio_bukovac.model.LoginResponse
import com.shows_antonio_bukovac.networking.ApiModule
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class UserViewModel : ViewModel() {


    fun addImage(image: MultipartBody.Part) {
        ApiModule.retrofit.addImage(image).enqueue(object :
            retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    Log.v("USPJEH", "uspjeh")
                }
                Log.v("USPJEH", "neuspjeh")
                Log.v("NEUSPJEH Message", response.message())
                Log.v("NEUSPJEH Body", response.body()?.user.toString())
                Log.v("NEUSPJEH Error body ", response.errorBody().toString())
                Log.v("NEUSPJEH Headers ", response.headers().toString())
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.v("USPJEH", "failure")
            }
        })
    }

}