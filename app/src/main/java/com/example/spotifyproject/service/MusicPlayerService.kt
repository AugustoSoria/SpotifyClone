package com.example.spotifyproject.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.spotifyproject.App
import com.example.spotifyproject.model.MusicPlayerAction

class MusicPlayerService : Service() {

    private var player: MediaPlayer? = null
    private val NOTIFICATION_ID = 1
    private var lastPosition: Int = -1
    private val notificationService = NotificationService(this)
    private val binder = MusicPlayerBinder()
    private var position = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val urlTrack = intent?.getStringExtra("SELECTED_TRACK_URL")
        position = intent?.getStringExtra("SELECTED_TRACK_POSITION")!!.toInt()

        if (urlTrack == null) { return START_STICKY }
        else if (intent?.action == MusicPlayerAction.PAUSE.toString()) { this.onPause() }
        else if (position == lastPosition && intent?.action == MusicPlayerAction.PLAY.toString()) { player?.start() }
        else {
            lastPosition = position

            if(isPlaying()) onDestroy()

            player = MediaPlayer()
            player?.setDataSource(urlTrack)
            player?.prepare()
            player?.isLooping = false
            player?.setOnCompletionListener { playNextTrack() }
            player?.start()
        }

        App.setCurrentPlayingTrack(position)
        val trackUpdatedIntent = Intent(App.trackUpdatedIntentAction)
        trackUpdatedIntent.putExtra("IS_PLAYING", isPlaying())
        sendBroadcast(trackUpdatedIntent)

        launchNotification()

        return START_STICKY
    }

    private fun playNextTrack() {
        val tracksSize = App.getPlaylistTracksSize()
        if (position != tracksSize - 1) {
            ++position
            lastPosition = position

            val nextTrack = App.getTrack(position)
            val nextTrackUrl = nextTrack?.preview_url

            if (nextTrackUrl != null) {
                val intent = Intent(applicationContext, MusicPlayerService::class.java)
                intent.action = "Play"
                intent.putExtra("SELECTED_TRACK_URL", nextTrackUrl)
                intent.putExtra("SELECTED_TRACK_POSITION", position.toString())

                startForegroundService(intent)
            }
        }
    }

    private fun launchNotification() {
        val musicNotification = notificationService.createNotification(position, isPlaying())
        if(musicNotification != null) startForeground(NOTIFICATION_ID, musicNotification)
    }

    inner class MusicPlayerBinder : Binder() {

        fun getService(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private fun onPause() {
        player?.pause()
    }

    fun getPlayerDuration(): Int {
        return player?.currentPosition ?: 0
    }

    fun getTrackPosition(): Int {
        return position
    }

    private fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stop()
    }

}
