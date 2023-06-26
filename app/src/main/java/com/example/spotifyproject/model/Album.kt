package com.example.spotifyproject.model

data class Album(
    val artists: List<Artist>,
    val id: String,
    val images: List<Image>,
    val name: String,
    val releaseDate: String,
    val total_tracks: Long,
    val type: String,
)