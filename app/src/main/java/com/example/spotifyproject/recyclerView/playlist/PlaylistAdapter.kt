package com.example.spotifyproject.recyclerView.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyproject.R
import com.example.spotifyproject.model.Playlist

class PlaylistAdapter(private val playlists: MutableList<Playlist>, private val onPlaylistClick: (Playlist) -> Unit) : RecyclerView.Adapter<PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(layoutInflater.inflate(R.layout.playlist_item, parent, false))
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val item = playlists[position]
        holder.bind(item, onPlaylistClick)
    }
}