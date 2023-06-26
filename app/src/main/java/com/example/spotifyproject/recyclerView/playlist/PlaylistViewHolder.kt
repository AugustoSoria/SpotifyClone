package com.example.spotifyproject.recyclerView.playlist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyproject.databinding.PlaylistItemBinding
import com.example.spotifyproject.model.Playlist
import com.squareup.picasso.Picasso

class PlaylistViewHolder (view: View): RecyclerView.ViewHolder(view) {
    private val binding = PlaylistItemBinding.bind(view)

    fun bind(playlist: Playlist, onPlaylistClick: (Playlist) -> Unit){
        Picasso.get().load(playlist.images[0].url).into(binding.ivPlaylist)
        binding.ivPlaylist.setOnClickListener{ onPlaylistClick(playlist) }
    }
}