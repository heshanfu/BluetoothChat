package com.glodanif.bluetoothchat.view

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import com.glodanif.bluetoothchat.R
import com.glodanif.bluetoothchat.activity.ChatActivity
import com.glodanif.bluetoothchat.activity.ConversationsActivity
import com.glodanif.bluetoothchat.service.BluetoothConnectionService
import com.glodanif.bluetoothchat.util.NotificationSettings

class NotificationViewImpl(private val context: Context) : NotificationView {

    private val notificationManager =
            context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
    private val resources = context.resources

    override fun getForegroundNotification(message: String): Notification {

        val notificationIntent = Intent(context, ConversationsActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        val stopIntent = Intent(context, BluetoothConnectionService::class.java)
        stopIntent.action = BluetoothConnectionService.ACTION_STOP
        val stopPendingIntent = PendingIntent.getService(context, 0, stopIntent, 0)

        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        val builder = Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_LOW)
                .addAction(0, context.getString(R.string.notification__stop), stopPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(resources.getColor(R.color.colorPrimary))
        }

        return builder.build()
    }

    override fun showNewMessageNotification(message: String, displayName: String?, deviceName: String, address: String, settings: NotificationSettings) {

        val notificationIntent = Intent(context, ChatActivity::class.java)
                .putExtra(ChatActivity.EXTRA_ADDRESS, address)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val name = if (displayName.isNullOrEmpty()) deviceName else "$displayName ($deviceName)"

        val builder = Notification.Builder(context)
                .setContentTitle(name)
                .setContentText(message)
                .setLights(Color.BLUE, 3000, 3000)
                .setSmallIcon(R.drawable.ic_new_message)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(resources.getColor(R.color.colorPrimary))
        }

        val notification = builder.build()

        if (settings.soundEnabled) {
            notification.defaults = notification.defaults or Notification.DEFAULT_SOUND
        }
        if (settings.vibrationEnabled) {
            notification.defaults = notification.defaults or Notification.DEFAULT_VIBRATE
        }

        notificationManager.notify(NotificationView.NOTIFICATION_TAG_MESSAGE,
                NotificationView.NOTIFICATION_ID_MESSAGE, notification)
    }

    override fun showConnectionRequestNotification(deviceName: String, settings: NotificationSettings) {

        val notificationIntent = Intent(context, ConversationsActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        val builder = Notification.Builder(context)
                .setContentTitle(context.getString(R.string.notification__connection_request))
                .setContentText(context.getString(R.string.notification__connection_request_body, deviceName))
                .setLights(Color.BLUE, 3000, 3000)
                .setSmallIcon(R.drawable.ic_connection_request)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(resources.getColor(R.color.colorPrimary))
        }

        val notification = builder.build()

        if (settings.soundEnabled) {
            notification.defaults = notification.defaults or Notification.DEFAULT_SOUND
        }
        if (settings.vibrationEnabled) {
            notification.defaults = notification.defaults or Notification.DEFAULT_VIBRATE
        }

        notificationManager.notify(NotificationView.NOTIFICATION_TAG_CONNECTION,
                NotificationView.NOTIFICATION_ID_CONNECTION, notification)
    }

    override fun dismissMessageNotification() {
        notificationManager.cancel(
                NotificationView.NOTIFICATION_TAG_MESSAGE,NotificationView.NOTIFICATION_ID_MESSAGE)
    }

    override fun dismissConnectionNotification() {
        notificationManager.cancel(
                NotificationView.NOTIFICATION_TAG_CONNECTION, NotificationView.NOTIFICATION_ID_CONNECTION)
    }
}