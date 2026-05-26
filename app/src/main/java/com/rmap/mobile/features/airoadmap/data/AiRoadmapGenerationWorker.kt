package com.rmap.mobile.features.airoadmap.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.rmap.mobile.MainActivity
import com.rmap.mobile.R
import kotlinx.coroutines.delay

class AiRoadmapGenerationWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val topic = inputData.getString(KEY_TOPIC).orEmpty().ifBlank {
            return Result.failure(workDataOf(KEY_ERROR_MESSAGE to ERROR_MISSING_TOPIC))
        }

        ensureNotificationChannel()
        updateProgress(topic = topic, progress = 0, stage = STAGE_STARTING)

        GENERATION_STAGES.forEachIndexed { index, stage ->
            if (isStopped) {
                return Result.failure(workDataOf(KEY_ERROR_MESSAGE to ERROR_CANCELLED))
            }

            val progress = ((index + 1).toFloat() / GENERATION_STAGES.size.toFloat() * 100).toInt()
            delay(STEP_DELAY_MILLIS)
            updateProgress(topic = topic, progress = progress, stage = stage)
        }

        showCompletionNotification(topic)

        return Result.success(
            workDataOf(
                KEY_ROADMAP_ID to GENERATED_ROADMAP_ID,
                KEY_PROGRESS to 100,
                KEY_STAGE to STAGE_READY,
                KEY_TOPIC to topic
            )
        )
    }

    private suspend fun updateProgress(topic: String, progress: Int, stage: String) {
        setProgress(
            workDataOf(
                KEY_PROGRESS to progress,
                KEY_STAGE to stage,
                KEY_TOPIC to topic
            )
        )
        setForeground(createForegroundInfo(topic = topic, progress = progress, stage = stage))
    }

    private fun createForegroundInfo(topic: String, progress: Int, stage: String): ForegroundInfo {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_rmap)
            .setContentTitle(context.getString(R.string.ai_roadmap_notification_title))
            .setContentText(context.getString(R.string.ai_roadmap_notification_body, topic, stage))
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    context.getString(R.string.ai_roadmap_notification_body, topic, stage)
                )
            )
            .setColor(NOTIFICATION_COLOR)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setProgress(100, progress.coerceIn(0, 100), progress == 0)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(openAppPendingIntent())
            .addAction(
                R.drawable.ic_notification_rmap,
                context.getString(R.string.ai_roadmap_notification_action_open),
                openAppPendingIntent()
            )
            .addAction(
                R.drawable.ic_notification_rmap,
                context.getString(R.string.ai_roadmap_notification_action_cancel),
                WorkManager.getInstance(context).createCancelPendingIntent(id)
            )
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                ONGOING_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(ONGOING_NOTIFICATION_ID, notification)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showCompletionNotification(topic: String) {
        if (!canPostNotifications()) return

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_rmap)
            .setContentTitle(context.getString(R.string.ai_roadmap_notification_ready_title))
            .setContentText(context.getString(R.string.ai_roadmap_notification_ready_body, topic))
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    context.getString(R.string.ai_roadmap_notification_ready_body, topic)
                )
            )
            .setColor(NOTIFICATION_COLOR)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openAppPendingIntent())
            .build()

        NotificationManagerCompat.from(context).notify(COMPLETION_NOTIFICATION_ID, notification)
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.ai_roadmap_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.ai_roadmap_notification_channel_description)
            setShowBadge(false)
        }

        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
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

    companion object {
        const val UNIQUE_WORK_NAME = "ai_roadmap_generation"
        const val KEY_TOPIC = "topic"
        const val KEY_DEADLINE_EPOCH_MILLIS = "deadline_epoch_millis"
        const val KEY_DAILY_STUDY_HOURS = "daily_study_hours"
        const val KEY_PROGRESS = "progress"
        const val KEY_STAGE = "stage"
        const val KEY_ROADMAP_ID = "roadmap_id"
        const val KEY_ERROR_MESSAGE = "error_message"
        const val CHANNEL_ID = "roadmap_generation"

        private const val REQUEST_CODE_OPEN_APP = 4401
        private const val ONGOING_NOTIFICATION_ID = 4501
        private const val COMPLETION_NOTIFICATION_ID = 4502
        private const val GENERATED_ROADMAP_ID = "frontend-pro"
        private const val ERROR_MISSING_TOPIC = "Missing roadmap topic"
        private const val ERROR_CANCELLED = "Roadmap generation was cancelled"
        private const val STAGE_STARTING = "Starting"
        private const val STAGE_READY = "Ready"
        private const val STEP_DELAY_MILLIS = 15_000L
        private val NOTIFICATION_COLOR = Color.rgb(43, 127, 255)
        private val GENERATION_STAGES = listOf(
            "Analyzing your target",
            "Estimating timeline",
            "Reading your answers",
            "Selecting skill nodes",
            "Sequencing milestones",
            "Finalizing roadmap"
        )
    }
}
