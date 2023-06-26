package com.example.spotifyproject.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.spotifyproject.model.MusicPlayerAction

class PlayerControlIntents {

    fun getNotiBtnPendingIntent(intentAction: MusicPlayerAction, position: Int, context: Context): PendingIntent {
        val intent = Intent(context, MusicPlayerControls::class.java)
        intent.action = intentAction.toString()
        intent.putExtra("SELECTED_TRACK_POSITION", position.toString())

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getBtnIntent(intentAction: MusicPlayerAction, position: Int, context: Context): Intent {
        val intent = Intent(context, MusicPlayerControls::class.java)
        intent.action = intentAction.toString()
        intent.putExtra("SELECTED_TRACK_POSITION", position.toString())

        return intent
    }

}