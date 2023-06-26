package com.example.spotifyproject.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.spotifyproject.App
import com.example.spotifyproject.model.MusicPlayerAction

class MusicPlayerControls : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        var musicIntent = Intent(context, MusicPlayerService::class.java)
        val tracksSize = App.getPlaylistTracksSize()
        var position = intent?.getStringExtra("SELECTED_TRACK_POSITION")?.toInt() ?: 0 // ver lo del null de la posicion
        musicIntent.action = intent?.action

        if (intent?.action == MusicPlayerAction.NEXT.toString()) {
            if(position >= tracksSize - 1) position = 0
            else position = position.plus(1)

        } else if (intent?.action == MusicPlayerAction.PREV.toString()) {
            if(position == 0) position = tracksSize - 1
            else position = position.minus(1)

        } else if (intent?.action == MusicPlayerAction.CLOSE.toString()) {
            context?.stopService(musicIntent)
            musicIntent.action = null
        }

        musicIntent = setExtras(musicIntent, position)

        if(musicIntent.action != null) context?.startForegroundService(musicIntent)
    }

    private fun setExtras(intent: Intent, position: Int): Intent {
        val currentTrack = App.getTrack(position) ?: return Intent("", null)

        return intent.apply {
            putExtra("SELECTED_TRACK_URL", currentTrack.preview_url)
            putExtra("SELECTED_TRACK_ALBUM_IMG", currentTrack.album.images[0].url)
            putExtra("SELECTED_TRACK_TITLE", currentTrack.name)
            putExtra("SELECTED_TRACK_ARTIST", App.parseArtists(currentTrack.artists))
            putExtra("SELECTED_TRACK_POSITION", position.toString())
            putExtra("SELECTED_TRACK_ID", currentTrack.id)
        }
    }
}