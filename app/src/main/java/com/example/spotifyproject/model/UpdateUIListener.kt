package com.example.spotifyproject.model

interface UpdateUIListener {
    fun onUpdateUI(currentTrack: Track, isPlaying: Boolean)
}