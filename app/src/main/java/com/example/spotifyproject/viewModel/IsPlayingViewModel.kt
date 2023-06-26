package com.example.spotifyproject.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IsPlayingViewModel : ViewModel() {
    val isPlayingModel = MutableLiveData<Boolean>()

    fun isPlaying(isPlaying: Boolean) {
        isPlayingModel.postValue(isPlaying)
    }
}