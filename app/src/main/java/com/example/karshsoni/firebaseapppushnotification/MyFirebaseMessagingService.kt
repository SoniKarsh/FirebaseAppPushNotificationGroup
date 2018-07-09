package com.example.karshsoni.firebaseapppushnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "MyFirebaseMsgingService"
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    var bundleNotificationId: Int = 100
    var singleNotificationId = 100
    var bundle_notification_id : String? = null
    val group_key = "com.example.karshsoni.bundlednotification"
    private val groupChannelId = "com.example.karshsoni.demosmsreceiverreadalarm"
    private val channelId = "com.example.karshsoni.demosmsreceiverreadalarm"
    private val text ="test"
    lateinit var builder:NotificationCompat.Builder
    lateinit var builder2:NotificationCompat.Builder
    var counter: Int = 0
    var notifications: ArrayList<String> = ArrayList()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        var notificationBody = remoteMessage!!.notification!!.body
        var notificationTitle = remoteMessage.notification!!.title
        var notificationData = remoteMessage.data.toString()
        Log.d(TAG, "onMessageReceived: notificationData $notificationData")
        Log.d(TAG, "onMessageReceived: notificationTitle $notificationTitle")
        Log.d(TAG, "onMessageReceived: notificationBody $notificationBody")

        notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        bundle_notification_id = "bundle_notification_" + bundleNotificationId;
        var resultIntent = Intent(this@MyFirebaseMessagingService, MainActivity::class.java)
        resultIntent.putExtra("notification", "Summary Notification Clicked")
        resultIntent.putExtra("notification_id", bundleNotificationId)
        resultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        var resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (notificationManager.notificationChannels.size < 2) {
                val groupChannel = NotificationChannel(groupChannelId, "bundle_channel_name", NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(groupChannel)
                val channel = NotificationChannel(channelId, "channel_name", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }
        }
        val summaryNotificationBuilder = NotificationCompat.Builder(this, "bundle_channel_id")
                .setGroup(bundle_notification_id)
                .setGroupSummary(true)
//                .setContentTitle("New Notification $singleNotificationId")
                .setContentTitle(notificationTitle)
//                .setContentText("Content for the notification")
                .setContentText(notificationBody)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(resultPendingIntent)
        if (singleNotificationId == bundleNotificationId)
            singleNotificationId = bundleNotificationId + 1
        else
            singleNotificationId++

        resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.putExtra("notification", "Single notification clicked")
        resultIntent.putExtra("notification_id", singleNotificationId)
        resultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        resultPendingIntent = PendingIntent.getActivity(this, singleNotificationId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this, "channel_id")
                .setGroup(bundle_notification_id)
//                .setContentTitle("New Notification $singleNotificationId")
                .setContentTitle(notificationTitle)
//                .setContentText("Content for the notification")
                .setContentText(notificationBody)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroupSummary(false) // False
                .setContentIntent(resultPendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notification.build())
        notificationManager.notify(bundleNotificationId, summaryNotificationBuilder.build())

//        val notificationIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
////        notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        notificationManager = NotificationManagerCompat.from(this@MyFirebaseMessagingService)
//        notificationChannel = NotificationChannel(channelId, text, NotificationManager.IMPORTANCE_HIGH)
//        notificationChannel.enableLights(true)
//        notificationChannel.lightColor = Color.BLUE
//        notificationChannel.enableVibration(false)
////        notificationManager.createNotificationChannel(notificationChannel)
//
//        builder = NotificationCompat.Builder(this@MyFirebaseMessagingService, channelId)
//                .setContentTitle(notificationTitle)
//                .setContentText(notificationBody)
//                .setGroup(group_key)
//                .setGroupSummary(true)
//                .setSmallIcon(R.drawable.notification_icon_background)
//                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.notification_icon_background))
//                .setContentIntent(pendingIntent)
//
//        val summaryNotification = NotificationCompat.Builder(this@MyFirebaseMessagingService, channelId)
//                .setContentTitle(notificationTitle)
//                //set content text to support devices running API level < 24
//                .setContentText("New messages")
//                .setSmallIcon(R.drawable.ic_action_name)
//                //build summary info into InboxStyle template
//                .setStyle(NotificationCompat.InboxStyle()
//                        .setBigContentTitle("2 new messages"))
//                //specify which group this notification belongs to
//                .setGroup(group_key)
//                //set this notification as the summary for the group
//                .setGroupSummary(true)
//                .build()
//
//        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
//
//        notificationManager.notify(3, summaryNotification)

    }


    override fun onDeletedMessages() {

    }

    override fun onNewToken(newToken: String?) {
        super.onNewToken(newToken)
        Log.d(TAG, "onNewToken: $newToken")

        sendRegistrationToServer(newToken!!)
    }

    private fun sendRegistrationToServer(token: String) {
        val dataBase = FirebaseDatabase.getInstance()
        var reference: DatabaseReference = dataBase.reference.child("User").child("MI")
        reference.child("token").setValue(token)
    }
}