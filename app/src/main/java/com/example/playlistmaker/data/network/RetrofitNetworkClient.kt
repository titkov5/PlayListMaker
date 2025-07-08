package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


class RetrofitNetworkClient: NetworkClient {
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    private val trackAPIService = retrofit.create<TrackApiService>()


    override fun doRequest(dto: Any): com.example.playlistmaker.data.dto.Response {
        if (dto is TrackSearchRequest) {
            val response: com.example.playlistmaker.data.dto.Response
            try {
                val response = trackAPIService.search(dto.searchText).execute()
                println("сюда я не пришел")
                val body = response.body() ?: com.example.playlistmaker.data.dto.Response()
                return body.apply { resultCode = response.code() }
            } catch (e: Exception) {
                println(e)
                //
                return com.example.playlistmaker.data.dto.Response().apply { resultCode = 400 }
            }


        } else {
            return com.example.playlistmaker.data.dto.Response().apply { resultCode = 400 }
        }
    }
}
