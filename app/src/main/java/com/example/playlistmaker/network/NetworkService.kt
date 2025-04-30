package com.example.playlistmaker.network

import com.example.playlistmaker.Search.Track
import com.example.playlistmaker.Search.TrackResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


object NetworkService {
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    private val trackService = retrofit.create<TrackApiService>()

    fun findTracks(
        text: String,
        completion: (List<Track>) -> Unit,
        failureCompletion: (String) -> Unit,
    ) {
        trackService.search(text).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>,
            ) {
                if (response.isSuccessful) {
                    completion(response.body()?.results ?: emptyList())
                } else {
                    failureCompletion(response.errorBody()?.toString() ?: "")
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                failureCompletion(t.toString())
            }
        }
        )
    }
}