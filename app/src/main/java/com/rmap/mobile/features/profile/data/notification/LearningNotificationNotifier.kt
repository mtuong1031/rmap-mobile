package com.rmap.mobile.features.profile.data.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.rmap.mobile.MainActivity
import com.rmap.mobile.R

class LearningNotificationNotifier(
    private val context: Application,
    private val streakStore: LearningStreakStore = LearningStreakStore(context),
    private val contentFactory: LearningNotificationContentFactory = LearningNotificationContentFactory(context)
) {
    fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_learning_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notification_channel_learning_description)
            enableVibration(true)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    fun showLearningReminder() {
        if (!canPostNotifications()) return

        ensureNotificationChannel()

        val title = contentFactory.reminderTitle()
        val body = contentFactory.reminderBody(streakStore.currentStreakDays())
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_rmap)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setColor(NOTIFICATION_COLOR)
            .setColorized(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent())
            .addAction(
                R.drawable.ic_notification_rmap,
                context.getString(R.string.notification_action_start_learning),
                openAppPendingIntent()
            )
            .addAction(
                R.drawable.ic_notification_rmap,
                context.getString(R.string.notification_action_mark_streak),
                markStreakPendingIntent()
            )
            .build()

        NotificationManagerCompat.from(context).notify(LEARNING_REMINDER_NOTIFICATION_ID, notification)
    }

    @SuppressLint("MissingPermission")
    fun showStreakCelebration(result: StreakCheckInResult) {
        if (!canPostNotifications()) return

        ensureNotificationChannel()
        NotificationManagerCompat.from(context).cancel(LEARNING_REMINDER_NOTIFICATION_ID)

        val title = contentFactory.streakCelebrationTitle(result)
        val body = contentFactory.streakCelebrationBody(result)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_rmap)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setColor(NOTIFICATION_COLOR)
            .setColorized(true)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent())
            .build()

        NotificationManagerCompat.from(context).notify(STREAK_CELEBRATION_NOTIFICATION_ID, notification)
    }

    private fun canPostNotifications(): Boolean {
        val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        val runtimePermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

        return notificationsEnabled && runtimePermissionGranted
    }

    private fun openAppPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        return PendingIntent.getActivity(
            context,
            REQUEST_CODE_OPEN_APP,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun markStreakPendingIntent(): PendingIntent {
        val intent = Intent(context, LearningReminderReceiver::class.java).apply {
            action = LearningReminderReceiver.ACTION_MARK_STREAK
        }

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_MARK_STREAK,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val CHANNEL_ID = "learning_reminders"
        private const val REQUEST_CODE_OPEN_APP = 4201
        private const val REQUEST_CODE_MARK_STREAK = 4202
        private const val LEARNING_REMINDER_NOTIFICATION_ID = 4301
        private const val STREAK_CELEBRATION_NOTIFICATION_ID = 4302
        private val NOTIFICATION_COLOR = Color.rgb(43, 127, 255)
    }
}
