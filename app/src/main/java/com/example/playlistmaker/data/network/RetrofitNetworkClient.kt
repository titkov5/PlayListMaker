package com.example.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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


class RetrofitNetworkClient(private val context: Context): NetworkClient {
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    private val trackAPIService = retrofit.create<TrackApiService>()

    override fun doRequest(dto: Any): com.example.playlistmaker.data.dto.Response {
        if (!isConnected()) {
            return com.example.playlistmaker.data.dto.Response().apply { resultCode = -1 }
        }
        if (dto is TrackSearchRequest) {
            try {
                val response = trackAPIService.search(dto.searchText).execute()

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

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
