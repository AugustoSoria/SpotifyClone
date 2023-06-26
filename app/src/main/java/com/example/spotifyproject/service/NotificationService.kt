package com.example.spotifyproject.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaSessionCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import androidx.core.app.NotificationCompat
import com.example.spotifyproject.App
import com.example.spotifyproject.R
import com.example.spotifyproject.model.MusicPlayerAction
import java.net.HttpURLConnection
import java.net.URL

class NotificationService(private val context: Context) {

    private val CHANNEL_ID = "musicChannel"
    private var albumBitmap: Bitmap? = null
    private val playerControlIntents = PlayerControlIntents()
    private var lastAlbumImgUrl: String? = null

    fun createMusicNotificationChannel() {
        val name = "Music channel"
        val descriptionText = "Channel to music notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    fun createNotification(position: Int, isPLaying: Boolean): Notification? {
        val currentPlayingTrack = App.currentPlayingTrack ?: return null

        val currentTrackAlbumImg = currentPlayingTrack.album.images[0].url
        val currentTitleTrack = currentPlayingTrack.name
        val currentArtistTrack = App.parseArtists(currentPlayingTrack.artists)
        val mediaSessionCompat = MediaSessionCompat(context, "tag")
        val middleBtnDrw: Int
        val middleBtnTitle: String

        getAlbumBitmap(currentTrackAlbumImg)

        val middleBtnPendingIntent: PendingIntent
        if(isPLaying) {
            middleBtnPendingIntent = playerControlIntents.getNotiBtnPendingIntent(MusicPlayerAction.PAUSE, position, context)
            middleBtnDrw = R.drawable.ic_grey_round_pause
            middleBtnTitle = MusicPlayerAction.PAUSE.toString()
        } else {
            middleBtnPendingIntent = playerControlIntents.getNotiBtnPendingIntent(MusicPlayerAction.PLAY, position, context)
            middleBtnDrw = R.drawable.ic_grey_round_play
            middleBtnTitle = MusicPlayerAction.PLAY.toString()
        }
        val nextPendingIntent: PendingIntent = playerControlIntents.getNotiBtnPendingIntent(MusicPlayerAction.NEXT, position, context)
        val prevPendingIntent: PendingIntent = playerControlIntents.getNotiBtnPendingIntent(MusicPlayerAction.PREV, position, context)
        val closePendingIntent: PendingIntent = playerControlIntents.getNotiBtnPendingIntent(MusicPlayerAction.CLOSE, position, context)


        val musicNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(currentTitleTrack)
            .setContentText(currentArtistTrack)
            .setLargeIcon(albumBitmap)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_grey_round_previous, MusicPlayerAction.PREV.toString(), prevPendingIntent)
            .addAction(middleBtnDrw, middleBtnTitle, middleBtnPendingIntent)
            .addAction(R.drawable.ic_grey_round_next, MusicPlayerAction.NEXT.toString(), nextPendingIntent)
            .addAction(R.drawable.ic_grey_round_close, MusicPlayerAction.CLOSE.toString(), closePendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2, 3)
                .setMediaSession(mediaSessionCompat.sessionToken))
            .build()

        return musicNotification
    }

    private fun getAlbumBitmap(albumImgUrl: String?) {
        if(albumImgUrl != lastAlbumImgUrl) {
            lastAlbumImgUrl = albumImgUrl

            runBlocking {
                async(Dispatchers.IO) {
                    try {
                        val url = URL(albumImgUrl)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.doInput = true
                        connection.connect()
                        albumBitmap =  BitmapFactory.decodeStream(connection.inputStream)
                    } catch(e: Exception) { albumBitmap = null }
                }.await()
            }
        }
    }
}