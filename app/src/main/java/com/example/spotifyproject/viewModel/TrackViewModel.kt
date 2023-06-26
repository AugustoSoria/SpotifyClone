package com.example.spotifyproject.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spotifyproject.App
import com.example.spotifyproject.model.Track

class TrackViewModel : ViewModel() {
    val currentTrackModel = MutableLiveData<Track?>()

    fun setPlayingTrack(currentTrack: Track) {
        currentTrackModel.postValue(currentTrack)
    }

    fun setPlayingTrack(currentTrackPosition: Int?) {
        if(currentTrackPosition != null) {
            val currentTrack = App.getTrack(currentTrackPosition)
            currentTrackModel.postValue(currentTrack)
        }
    }
}