package com.courierdriver.firebaseMobile


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.courierdriver.R
import com.courierdriver.model.GcmMessageResponse
import com.courierdriver.views.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONException

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var userId = ""
    private var response: GcmMessageResponse? = null
    //*
    //* Called when message is received.
    //*
    //* @param remoteMessage Object representing the message received from Firebase Cloud Messaging.


    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: e.from}\${remoteMessag")
        var message = remoteMessage.notification
        Log.d(TAG, "DATA: ${remoteMessage.notification}")

        if (remoteMessage == null)
            return

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            /*if (Check if data needs to be processed by long running job*//*true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
            }*/

            try {
                val data = remoteMessage.data
                handleNotification(data)
            } catch (e: Exception) {
                Log.e(TAG, "Data Payload: " + remoteMessage.data.toString())
            }
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    //*
    //* Called if InstanceID token is updated. This may occur if the security of
    //* the previous token had been compromised. Note that this is called when the InstanceID token
    //* is initially generated so this is where you would retrieve the token.


    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]


    //*
    //* Persist token to third-party servers.
    //*
    //* Modify this method to associate the user's FCM InstanceID token with any server-side account
    //* maintained by your application.
    //*
    //* @param token The new token
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    @Throws(JSONException::class)
    private fun handleNotification(dataList: Map<String, String>) {

        if (!dataList.containsKey("message")) {
            return
        }
        val message = dataList["message"]
        val gson = Gson()
        response = gson.fromJson(message, GcmMessageResponse::class.java)
        if (response != null) {
            var requestType = response!!.notificationResponse!!.requestType!!
            var displayMessage = response!!.notificationResponse!!.message.toString()
            var contractType = response!!.notificationResponse!!.contractType
            if (requestType == "")
                return
            val intent: Intent?
            when (requestType) {
                "jnhjnj" -> {
                    intent = Intent(this, SplashActivity::class.java)
                    intent.putExtra(
                        "coming_from",
                        "notification"
                    )// activity if sending from notification
                    intent.putExtra("request_type", requestType)
                }

                else -> {
                    intent = Intent(this, SplashActivity::class.java)
                    // intent.putExtra("request_id", requestId);
                    intent.putExtra("request_type", requestType)
                    intent.putExtra("coming_from", "notification")
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            val pendingIntent = PendingIntent.getActivity(
                this, 1, intent,
                PendingIntent.FLAG_ONE_SHOT
            )

            // play notification sound
            //val notificationUtils = NotificationUtils(applicationContext)
            // notificationUtils.showCustomNotification(message_);
            //notificationUtils.playNotificationSound()
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder: NotificationCompat.Builder?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationBuilder = NotificationCompat.Builder(this, packageName)
                    .setSmallIcon(R.drawable.ic_app)
                    .setContentTitle(getString(R.string.app_name))
                    .setChannelId(packageName)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setContentText(displayMessage)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.FLAG_HIGH_PRIORITY)
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
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationManager.createNotificationChannel(channel)
                }
            } else {
                notificationBuilder = NotificationCompat.Builder(this, packageName)
                    .setSmallIcon(R.drawable.ic_app)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(displayMessage)
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

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
