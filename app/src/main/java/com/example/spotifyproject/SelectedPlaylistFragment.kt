package com.example.spotifyproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.Fragment
import com.example.spotifyproject.databinding.FragmentSelectedPlaylistBinding
import com.example.spotifyproject.model.*
import com.example.spotifyproject.recyclerView.playlistTracks.PlaylistTracksAdapter
import com.example.spotifyproject.service.PlayerControlIntents
import com.example.spotifyproject.viewModel.TrackViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class SelectedPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentSelectedPlaylistBinding
    private lateinit var adapter: PlaylistTracksAdapter
    private val playerControlIntents = PlayerControlIntents()
    private val trackViewModel: TrackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectedPlaylistBinding.inflate(layoutInflater, container, false)

        val playlistImg = arguments?.getString("playlistImg")
        val playlistUri = arguments?.getString("playlistUri")
        val playlistDesc = arguments?.getString("playlistDesc")
        val playlistFollowers = arguments?.getString("playlistFollowers") ?: "0"

        if(playlistUri != null) {
            val playlistId = playlistUri.replace("spotify:playlist:", "")

            val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale.getDefault()))
            val parsedFollowers = formatter.format(playlistFollowers.toLong()).replace(",", ".")
            binding.tvPlaylistFollowers.text = "$parsedFollowers me gusta"

            runBlocking {
                async(Dispatchers.IO) {
                    getPlaylistTracks(playlistId)
                }.await().let {
                    var playlistMlDuration: Long = 0
                    val playlistMinDuration: Double

                    Picasso.get().load(playlistImg).into(binding.ivPlaylist)
                    binding.tvPlaylistDesc.text = playlistDesc

                    App.tracks?.items?.forEach { item -> playlistMlDuration += item?.track?.duration_ms!! }
                    val playlistHrDuration: Double = (((playlistMlDuration / 1000).toDouble() / 60) / 60)
                    playlistMinDuration = (playlistHrDuration - playlistHrDuration.toInt()) * 60
                    val playlistDuration = "${playlistHrDuration.toInt()} h ${playlistMinDuration.toInt()} min"

                    binding.tvPlaylistDuration.text = "$playlistDuration"

                    binding.btnPlayCircle.setOnClickListener {
                        playTrack(null, 0)
                    }

                    initTracksRecyclerView()
                }
            }
        }

        return binding.root
    }

    private fun initTracksRecyclerView() {
        this.activity?.runOnUiThread {
            adapter = PlaylistTracksAdapter(App.tracks!!.items) { track, position -> playTrack(track, position) }
            binding.rvPlaylistTracks.layoutManager = LinearLayoutManager(this.context)
            binding.rvPlaylistTracks.adapter = adapter
        }
    }

    private fun playTrack(track: Track?, position: Int) {
        val intent = playerControlIntents.getBtnIntent(MusicPlayerAction.PLAY, position, requireContext())
        requireContext().sendBroadcast(intent)
        setGlobalStateTrack(track, position)
    }

    private fun setGlobalStateTrack(track: Track?, position: Int) {
        if(track == null) {
            trackViewModel.setPlayingTrack(position)
            App.setCurrentPlayingTrack(position)
        } else {
            trackViewModel.setPlayingTrack(track)
            App.currentPlayingTrack = track
        }
    }

    private suspend fun getPlaylistTracks(playlistId: String) {
//        runBlocking {
//            val call = async(Dispatchers.Default) { App.retrofitHelper.getPlaylistTracks(playlistId) }
//            val response = call.await()
//            val tracksPlaylistResponse = response.await()
//            if(tracksPlaylistResponse.isSuccessful) {
//                App.tracks = tracksPlaylistResponse.body()
//            }
//        }

        runBlocking {
            val call = async(Dispatchers.Default) { App.retrofitHelper.getMockPlaylistTracks() }
            val response = call.await()
            val tracksPlaylistResponse = response.await()
            if(tracksPlaylistResponse.isSuccessful) {
                App.tracks = tracksPlaylistResponse.body()
            }
        }

    }

}