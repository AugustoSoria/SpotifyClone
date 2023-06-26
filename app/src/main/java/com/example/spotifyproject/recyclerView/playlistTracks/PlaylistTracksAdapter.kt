package com.example.spotifyproject.recyclerView.playlistTracks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyproject.R
import com.example.spotifyproject.model.Item
import com.example.spotifyproject.model.Track

class PlaylistTracksAdapter(
    private val items: List<Item?>,
    private val onPlaylistTrackClick: (Track, Int) -> Unit
) : RecyclerView.Adapter<PlaylistTracksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistTracksViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlaylistTracksViewHolder(layoutInflater.inflate(R.layout.playlist_track_item, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PlaylistTracksViewHolder, position: Int) {
        val item = items[position]
        if (item != null) {
            holder.bind(item, onPlaylistTrackClick)
        }
    }
}