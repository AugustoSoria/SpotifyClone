package com.example.spotifyproject.model

data class Track(
    val album: Album,
    val artists: List<Artist>,
    val duration_ms: Long,
    val id: String,
    val name: String,
    val popularity: Long,
    val preview_url: String,
    val track_number: Long,
)