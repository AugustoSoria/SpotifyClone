package com.example.spotifyproject.recyclerView.playlistTracks

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyproject.App
import com.example.spotifyproject.databinding.PlaylistTrackItemBinding
import com.example.spotifyproject.model.Item
import com.example.spotifyproject.model.Track
import com.squareup.picasso.Picasso

class PlaylistTracksViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = PlaylistTrackItemBinding.bind(view)

    fun bind(item: Item, onPlaylistTrackClick: (Track, Int) -> Unit) {
        Picasso.get().load(item.track.album.images[0].url).into(binding.ivTrack)
        binding.tvTrackName.text = item.track.name
        binding.tvTrackArtist.text = App.parseArtists(item.track.artists)
        binding.clPlaylistTrack.setOnClickListener{ onPlaylistTrackClick(item.track, adapterPosition) }
    }
}