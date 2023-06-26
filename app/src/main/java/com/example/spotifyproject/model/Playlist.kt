package com.example.spotifyproject.model

data class Playlist (
   val description: String,
   val followers: Followers,
   val images: List<Image>,
   val name: String,
   val uri: String,
)