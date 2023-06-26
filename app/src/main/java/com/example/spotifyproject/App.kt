package com.example.spotifyproject

import android.app.Application
import com.example.spotifyproject.model.Artist
import com.example.spotifyproject.model.PlaylistTracks
import com.example.spotifyproject.model.Track
import com.example.spotifyproject.service.NotificationService
import com.example.spotifyproject.service.RetrofitHelper

class App: Application() {
    companion object {
        lateinit var retrofitHelper: RetrofitHelper
        var tracks: PlaylistTracks? = null
        var currentPlayingTrack: Track? = null
        val trackUpdatedIntentAction = "TRACK_UPDATED_ACTION"

        fun getPlaylistTracksSize(): Int {
            if(tracks?.items?.size == null) return -1
            else return tracks!!.items.size
        }

        fun getTrack(position: Int): Track? {
            return tracks?.items?.get(position)?.track
        }

        fun setCurrentPlayingTrack(position: Int) {
            currentPlayingTrack = getTrack(position)
        }

        fun getCurrentPlayingTrackPosition(): Int {
            val item = tracks?.items?.find { item -> item?.track == currentPlayingTrack }
            return tracks?.items?.indexOf(item) ?: -1
        }

        fun parseArtists(artists: List<Artist>): String {
            val resp: MutableList<String> = arrayListOf()
            artists.forEach { artist -> resp.add(artist.name) }
            return resp.joinToString(", ")
        }
    }

    override fun onCreate() {
        super.onCreate()
        retrofitHelper = RetrofitHelper(applicationContext)
        val notificationService = NotificationService(applicationContext)
        notificationService.createMusicNotificationChannel()
    }
}