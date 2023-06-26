package com.example.spotifyproject.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.spotifyproject.App
import com.example.spotifyproject.model.UpdateUIListener

class TrackUpdateReceiver(private val updateUIListener: UpdateUIListener) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val currentTrack = App.currentPlayingTrack
        val isPlaying = intent?.getBooleanExtra("IS_PLAYING", false) ?: false
        if(currentTrack != null) updateUIListener.onUpdateUI(currentTrack, isPlaying)
    }
}