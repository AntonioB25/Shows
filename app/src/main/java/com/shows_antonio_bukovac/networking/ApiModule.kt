package com.shows_antonio_bukovac.networking

import android.content.Context
import android.content.SharedPreferences
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.shows_antonio_bukovac.model.PrefsConstants
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.internal.addHeaderLenient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


object ApiModule {
    private const val BASE_URL = "https://tv-shows.infinum.academy"

    lateinit var retrofit: ShowsApiService

    fun initRetrofit(preferences: SharedPreferences, context: Context) {
        val okhttp = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(ChuckerInterceptor(context))
            .addInterceptor(Interceptor {
                val headerAcToken = preferences.getString("access-token", "")
                val headerClient = preferences.getString("client","").toString()
                val headerUid = preferences.getString("uid","").toString()

                if(!headerAcToken.isNullOrEmpty()){
                    val request = it.request().newBuilder()
                        .addHeader(PrefsConstants.HEADER_TOKEN_TYPE,"Bearer")
                        .addHeader(PrefsConstants.HEADER_ACCESS_TOKEN, headerAcToken)
                        .addHeader(PrefsConstants.HEADER_CLIENT,headerClient)
                        .addHeader(PrefsConstants.HEADER_UID,headerUid)
                        .build()
                    val response = it.proceed(request)
                    response
                }else {
                    it.proceed(it.request())
                }
            })
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(okhttp)
            .build()
            .create(ShowsApiService::class.java)
    }
}