package com.courierdriver.firebaseMobile


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.courierdriver.R
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.model.GcmMessageResponse
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.views.notification.NotificationChatActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    //*
    //* Called when message is received.
    //*
    //* @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
    // [START receive_message]
    private var response: GcmMessageResponse? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        // sendNotification(data.get("").toString())
        remoteMessage.notification
        Log.d("Notification--", "Received")
        // val data = remoteMessage.data
        sendNotification(data)
        data.get("body").toString()/*,
            data.get("status").toString(),
            data.get("message").toString()
        )*/
    }

    override fun onNewToken(token: String) {
        if (!TextUtils.isEmpty(token)) {
            SharedPrefClass().putObject(
                applicationContext,
                GlobalConstants.NOTIFICATION_TOKENPref,
                token
            )
            GlobalConstants.NOTIFICATION_TOKEN = token
        }

        Log.d("token", token + "")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }

    //* @param messageBody FCM message body received.
    private fun sendNotification(dataList: MutableMap<String, String>) {
        if (!dataList.containsKey("message")) {
            return
        }
        val displayMessage = dataList["message"]
        val intent = Intent(this, NotificationChatActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0/*Request code*/, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = getString(R.string.app_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = NotificationCompat.Builder(this, packageName)
                .setSmallIcon(R.drawable.ic_app)
                .setContentTitle(displayMessage)
                .setChannelId(packageName)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(false)
//                .setContentText(displayMessage)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(displayMessage)
                )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    packageName,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
        } else {
            notificationBuilder = NotificationCompat.Builder(this, packageName)
                .setSmallIcon(R.drawable.ic_app)
                .setContentTitle(displayMessage)
//                .setContentText(displayMessage)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder!!.setSmallIcon(R.drawable.ic_app)
        } else {
            notificationBuilder!!.setSmallIcon(R.drawable.ic_app)
        }
        //        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        val notificationManager = NotificationManagerCompat.from(this)
        notificationBuilder.priority = Notification.PRIORITY_MAX
        notificationManager.notify(0, notificationBuilder.build())
    }
}

