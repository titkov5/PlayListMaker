package com.example.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackSearchRequest

class RetrofitNetworkClient(private val trackAPIService:TrackApiService,
    private val context: Context): NetworkClient {

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
