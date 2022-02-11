package com.shows_antonio_bukovac

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shows_antonio_bukovac.databinding.ActivityMainBinding
import com.shows_antonio_bukovac.networking.ApiModule

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApiModule.initRetrofit(getPreferences(Context.MODE_PRIVATE), this)

    }
}