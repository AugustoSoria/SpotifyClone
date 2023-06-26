package com.example.spotifyproject.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getPlaylist(@Url url:String):Response<Playlist>

    @GET
    suspend fun getPlaylistTracks(@Url url:String):Response<PlaylistTracks>
}