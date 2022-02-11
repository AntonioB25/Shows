package com.shows_antonio_bukovac.view_models

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shows_antonio_bukovac.model.*
import com.shows_antonio_bukovac.networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel(){

    private val loginResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun getLoginResultLiveData(): LiveData<Boolean> {
        return loginResultLiveData
    }

    //TODO: Add SharedPreferences as method argument
    fun signIn(email: String, password: String, prefs: SharedPreferences?) {
        ApiModule.retrofit.signIn(LoginRequest(email, password)).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val headerAcToken = response.headers()[PrefsConstants.HEADER_ACCESS_TOKEN]
                    val headerClient = response.headers()[PrefsConstants.HEADER_CLIENT]
                    val headerUid = response.headers()[PrefsConstants.HEADER_UID]

                    prefs?.edit()?.putString(PrefsConstants.HEADER_ACCESS_TOKEN, headerAcToken)?.apply()
                    prefs?.edit()?.putString(PrefsConstants.HEADER_CLIENT, headerClient)?.apply()
                    prefs?.edit()?.putString(PrefsConstants.HEADER_UID, headerUid)?.apply()
                }
                loginResultLiveData.value = response.isSuccessful
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResultLiveData.value = false
            }
        })
    }

}