package com.teamdefine.legalvault

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MyFirebaseMessagingService(private val context: Context) : FirebaseMessagingService() {
    @SuppressLint("ServiceCast")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationTitle = remoteMessage.data["title"]
        val notificationBody = remoteMessage.data["body"]
        Timber.i("hello there")
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = "your_channel_id" // Set your own channel ID

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "Your Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle(notificationTitle)
            .setContentText(notificationBody)
            .setSmallIcon(R.drawable.appicon_legalvault)
            .setAutoCancel(true)

        val notificationId = 1 // Use a unique ID for each notification
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}
