package com.example.spotifyproject

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyproject.databinding.ActivityMainBinding
import com.example.spotifyproject.model.MusicPlayerAction
import com.example.spotifyproject.model.Playlist
import com.example.spotifyproject.model.Track
import com.example.spotifyproject.model.UpdateUIListener
import com.example.spotifyproject.recyclerView.playlist.PlaylistAdapter
import com.example.spotifyproject.service.PlayerControlIntents
import com.example.spotifyproject.service.TrackUpdateReceiver
import com.example.spotifyproject.viewModel.IsPlayingViewModel
import com.example.spotifyproject.viewModel.TrackViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), UpdateUIListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PlaylistAdapter
    private var playlists: MutableList<Playlist> = arrayListOf()
    private val trackViewModel: TrackViewModel by viewModels()
    private val isPlayingViewModel: IsPlayingViewModel by viewModels()
    private val playlistFragment = SelectedPlaylistFragment()
    private val bigCurrentTrackFragment = BigCurrentTrackFragment()
    private val playerControlIntents = PlayerControlIntents()
    private var isPlaying: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runBlocking {
            initHomePlaylist()
        }

        binding.tvTrackName.isSelected = true
        binding.clCurrentTrackBtn.setOnClickListener { middleBtnAction() }

        binding.clCurrentTrack.setOnClickListener {
            it.visibility = View.INVISIBLE
            showBigCurrentTrackFragment()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!bigCurrentTrackFragment.isRemoving) {
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.remove(bigCurrentTrackFragment)
                    fragmentTransaction.commit()

                    binding.clCurrentTrack.visibility = View.VISIBLE
                } else {
                    remove()
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        trackViewModel.currentTrackModel.observe(this) { showCurrentTrackView(it) }

        isPlayingViewModel.isPlayingModel.observe(this) { isPlaying ->
            this.isPlaying = isPlaying
            setMiddleBtnImage()
        }

        registerReceiver(trackUpdateReceiver, IntentFilter(App.trackUpdatedIntentAction))
    }

    private suspend fun initHomePlaylist() {
        runBlocking {
//            var playlist1: Playlist? = null
//            var playlist2: Playlist? = null
//
//            async(Dispatchers.IO) {
//                playlist1 = getPlaylist("37i9dQZF1DWSo7PX7dbgH8")
//                playlist2 =  getPlaylist("37i9dQZF1DZ06evO2iBPiw")
//            }.await().let {
//                if (playlist1 != null) playlists.add(playlist1!!)
//                if (playlist2 != null) playlists.add(playlist2!!)
//
//                initRecyclerView()
//            }

            var playlist1: Playlist? = null
            async(Dispatchers.IO) {
                playlist1 = getPlaylist("")
            }.await().let {
                if (playlist1 != null) playlists.add(playlist1!!)

                initRecyclerView()
            }
        }
    }

    private fun getPlaylist(playlistId: String): Playlist? {
        var playlist: Playlist? = null

//        runBlocking {
//            val call = async(Dispatchers.Default) { App.retrofitHelper.getPlaylist("$playlistId") }
//            val response = call.await()
//            val playlistResponse = response.await()
//            if(playlistResponse.isSuccessful) {
//                playlist = playlistResponse.body()
//            }
//        }
        runBlocking {
            val call = async(Dispatchers.Default) { App.retrofitHelper.getMockPlaylist() }
            val response = call.await()
            val playlistResponse = response.await()
            if(playlistResponse.isSuccessful) {
                playlist = playlistResponse.body()
            }
        }

        return playlist
    }

    private fun initRecyclerView() {
        runOnUiThread {
            adapter = PlaylistAdapter(playlists) { onPlaylistClick(it) }
            binding.rvPlaylist.layoutManager = GridLayoutManager(this, 2)
            binding.rvPlaylist.adapter = adapter
        }
    }

    private fun onPlaylistClick(playlist: Playlist) {
        if(playlistFragment.isAdded) return;
        val bundle = Bundle()
        bundle.putString("playlistUri", playlist.uri)
        bundle.putString("playlistImg", playlist.images[0].url)
        bundle.putString("playlistDesc", playlist.description)
        bundle.putString("playlistFollowers", playlist.followers.total.toString())
        playlistFragment.arguments = bundle

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .add(R.id.flPlaylist, playlistFragment, "playlistFragment")
            .addToBackStack( null)

        fragmentTransaction.commit()
    }

    private fun showCurrentTrackView(currentTrack: Track?) {
        if (currentTrack != null) {
            Picasso.get().load(currentTrack.album.images[0].url).into(binding.ivTrackAlbum)
            val artists = App.parseArtists(currentTrack.artists)
            binding.tvTrackName.text = "${currentTrack.name}  • $artists"
        }

        if(!bigCurrentTrackFragment.isRemoving) {
            binding.clCurrentTrack.visibility = View.VISIBLE
        }
    }

    private fun middleBtnAction() {
        val btnIntent: Intent
        val currentPosition = App.getCurrentPlayingTrackPosition()

        if(isPlaying) {
            btnIntent = playerControlIntents.getBtnIntent(MusicPlayerAction.PAUSE, currentPosition, this)
        } else {
            btnIntent = playerControlIntents.getBtnIntent(MusicPlayerAction.PLAY, currentPosition, this)
        }

        sendBroadcast(btnIntent)
    }

    private fun showBigCurrentTrackFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            .add(R.id.flPlaylist, bigCurrentTrackFragment, "bigCurrentTrackFragment")
            .addToBackStack(null)

        fragmentTransaction.commit()
    }

    private val trackUpdateReceiver: TrackUpdateReceiver by lazy {
        TrackUpdateReceiver(this)
    }

    override fun onUpdateUI(currentTrack: Track, isPlaying: Boolean) {
        trackViewModel.setPlayingTrack(currentTrack)
        isPlayingViewModel.isPlaying(isPlaying)
        setMiddleBtnImage()
    }

    private fun setMiddleBtnImage() {
        if(!isPlaying) {
            binding.clCurrentTrackBtn.setImageResource(R.drawable.ic_white_round_play)
        } else {
            binding.clCurrentTrackBtn.setImageResource(R.drawable.ic_white_round_pause)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(trackUpdateReceiver)
    }

}