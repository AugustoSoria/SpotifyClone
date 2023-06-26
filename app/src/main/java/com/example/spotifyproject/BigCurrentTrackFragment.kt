package com.example.spotifyproject

import android.content.*
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spotifyproject.databinding.FragmentBigCurrentTrackBinding
import com.example.spotifyproject.model.MusicPlayerAction
import com.example.spotifyproject.model.Track
import com.example.spotifyproject.model.UpdateUIListener
import com.example.spotifyproject.service.MusicPlayerService
import com.example.spotifyproject.service.PlayerControlIntents
import com.example.spotifyproject.service.TrackUpdateReceiver
import com.example.spotifyproject.viewModel.IsPlayingViewModel
import com.example.spotifyproject.viewModel.TrackViewModel
import com.squareup.picasso.Picasso

class BigCurrentTrackFragment : Fragment(), UpdateUIListener {
    private lateinit var binding: FragmentBigCurrentTrackBinding
    private val trackViewModel: TrackViewModel by activityViewModels()
    private val isPlayingViewModel: IsPlayingViewModel by activityViewModels()
    private val playerControlIntents = PlayerControlIntents()
    private var isServiceConnected = false
    private var isPlaying: Boolean = true
    private var position = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBigCurrentTrackBinding.inflate(layoutInflater, container, false)

        binding.trackProgressBar.progressTintList = ColorStateList.valueOf(Color.WHITE)
        binding.tvTrackName.isSelected = true
        binding.tvTrackArtist.isSelected = true

        trackViewModel.currentTrackModel.observe(this) { currentTrack ->
            if (currentTrack != null) {
                Picasso.get().load(currentTrack.album.images[0].url).into(binding.ivTrackAlbum)
                binding.tvTrackName.text = currentTrack.name
                binding.tvTrackArtist.text = App.parseArtists(currentTrack.artists)
                binding.trackProgressBar.min = 0
                binding.trackProgressBar.max = currentTrack.duration_ms.toInt()
                binding.totalTrackDuration.text = parseTrackProgress(millisecondsToMinutes(currentTrack.duration_ms))
            }
        }

        isPlayingViewModel.isPlayingModel.observe(this) { isPlaying ->
            this.isPlaying = isPlaying
            setMiddleBtnImage()
        }

        binding.btnPrev.setOnClickListener { prevSong() }

        binding.middleBtn.setOnClickListener { middleBtnAction() }

        binding.btnNext.setOnClickListener { nextSong() }

        binding.btnHiddenFragment.setOnClickListener {
            parentFragmentManager.popBackStack()
            activity?.findViewById<ConstraintLayout>(R.id.clCurrentTrack)?.visibility = View.VISIBLE
            this.onDestroy()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(requireContext(), MusicPlayerService::class.java)
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        requireContext().registerReceiver(trackUpdateReceiver, IntentFilter(App.trackUpdatedIntentAction))
    }

    private fun prevSong() {
        val prevBtnIntent: Intent = playerControlIntents.getBtnIntent(MusicPlayerAction.PREV, position, requireContext())
        requireContext().sendBroadcast(prevBtnIntent)
    }

    private fun middleBtnAction() {
        val middleBtnIntent: Intent

        if(isPlaying) {
            middleBtnIntent = playerControlIntents.getBtnIntent(MusicPlayerAction.PAUSE, position, requireContext())
        } else {
            middleBtnIntent = playerControlIntents.getBtnIntent(MusicPlayerAction.PLAY, position, requireContext())
        }

        requireContext().sendBroadcast(middleBtnIntent)
    }

    private fun nextSong() {
        val nextBtnIntent: Intent = playerControlIntents.getBtnIntent(MusicPlayerAction.NEXT, position, requireContext())
        requireContext().sendBroadcast(nextBtnIntent)
    }

    private fun millisecondsToMinutes(milliseconds: Long): Double {
        val minutes = milliseconds / (1000 * 60)
        val seconds = ((milliseconds % (1000 * 60)) / 1000).toDouble()
        return minutes + seconds / 60
    }

    private fun parseTrackProgress(time: Double): String {
        return String.format("%.2f", time).replace(".", ":")
    }

    private val serviceConnection = object : ServiceConnection {
        private var musicPlayerService: MusicPlayerService? = null
        private val updateInterval = 1000L // Update interval in milliseconds

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // Service connected, perform any necessary initialization or setup
            // Cast the IBinder to your custom binder class and communicate with the service
            val binder = service as MusicPlayerService.MusicPlayerBinder
            musicPlayerService = binder.getService()
            position = musicPlayerService!!.getTrackPosition()

            // Start updating the progress bar
            if (!isServiceConnected) {
                isServiceConnected = true
                update()
            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Service disconnected, clean up or handle any necessary tasks
            musicPlayerService = null
            requireContext().unbindService(this)
        }

        private fun update() {
            if(musicPlayerService != null) {
                val currentPosition = musicPlayerService!!.getPlayerDuration()
//                val playingTrack = musicPlayerService!!.getPlayingTrackId()
//                isPlaying = musicPlayerService!!.isPlaying()
                binding.trackProgressBar.progress = currentPosition
                binding.currentPlayerTimePosition.text = parseTrackProgress(millisecondsToMinutes(currentPosition.toLong()))
//                trackViewModel.setPlayingTrack(playingTrack)

                // Schedule the next update
                if (isServiceConnected) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        update()
                    }, updateInterval)
                }
            }
        }
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
        if(isPlaying) {
            binding.middleBtn.setImageResource(R.drawable.ic_white_round_pause_circle)
        } else {
            binding.middleBtn.setImageResource(R.drawable.ic_white_round_play_circle)
        }
    }

    override fun onPause() {
        super.onPause()
        requireContext().unbindService(serviceConnection)
        requireContext().unregisterReceiver(trackUpdateReceiver)
    }

}