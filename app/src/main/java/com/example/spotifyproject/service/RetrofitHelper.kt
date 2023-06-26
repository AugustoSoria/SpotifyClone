package com.example.spotifyproject.service

import android.content.Context
import com.example.spotifyproject.R
import com.example.spotifyproject.model.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class RetrofitHelper(context: Context) {

    suspend fun getPlaylist(playlistId: String) = CoroutineScope(Dispatchers.IO).async {
        getRetrofit()
            .create(APIService::class.java)
            .getPlaylist("/playlist/?id=$playlistId")
    }

    suspend fun getMockPlaylist() = CoroutineScope(Dispatchers.IO).async {
        getRetrofitToMock()
            .create(APIService::class.java)
            .getPlaylist("getBeatlesPlaylist/")
    }

    suspend fun getPlaylistTracks(playlistId: String) = CoroutineScope(Dispatchers.IO).async {
        getRetrofit()
            .create(APIService::class.java)
            .getPlaylistTracks("playlist_tracks/?id=$playlistId&offset=0&limit=30")
    }

    suspend fun getMockPlaylistTracks() = CoroutineScope(Dispatchers.IO).async {
        getRetrofitToMock()
            .create(APIService::class.java)
            .getPlaylistTracks("getBeatlesPlaylistTrack/")
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.request().newBuilder()
                .addHeader("X-RapidAPI-Key", context.getString(R.string.spotify_api_key))
                .addHeader("X-RapidAPI-Host", "spotify23.p.rapidapi.com")
                .build()
                .let(chain::proceed)
        }
        .connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://spotify23.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getRetrofitToMock(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://demo2372600.mockable.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}