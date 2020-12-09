package com.petarsrzentic.insurance.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.petarsrzentic.insurance.R
import com.petarsrzentic.insurance.ui.MainActivity

private const val NOTIFICATION_ID = 0

fun sendNotification(messageBody: String, applicationContext: Context) {

    val contentIntent = Intent(applicationContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
        applicationContext, NOTIFICATION_ID, contentIntent, 0
    )

    val builder = NotificationCompat.Builder(
        applicationContext, applicationContext.getString(R.string.insurance_id)
    )
        .setChannelId(R.string.insurance_id.toString())
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(messageBody)
        )
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle("Insurance")
        .setContentText(messageBody)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    with(NotificationManagerCompat.from(applicationContext)) {

        notify(NOTIFICATION_ID, builder.build())
    }


    Log.d("TAG", "fun NotificationManager.sendNotification")
}