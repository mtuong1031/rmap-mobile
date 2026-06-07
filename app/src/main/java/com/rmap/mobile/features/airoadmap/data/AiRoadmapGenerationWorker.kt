package com.rmap.mobile.features.airoadmap.data

import android.Manifest
import android.annotation.SuppressLint
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
import com.rmap.mobile.core.network.ApiClient
import com.rmap.mobile.core.network.NetworkResult
import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.SessionCookieJar
import com.rmap.mobile.core.storage.SharedPreferencesSessionCookieStorage
import com.rmap.mobile.features.airoadmap.data.mapper.toBackendRoleCategory
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.rmap.mobile.features.airoadmap.data.mapper.toDto
import com.rmap.mobile.features.airoadmap.data.model.GenerateRoadmapResponseDto
import com.rmap.mobile.features.airoadmap.data.remote.AiRoadmapApi
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapAnswer
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapDraft
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationRequest

class AiRoadmapGenerationWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val goal = inputData.getString(KEY_GOAL).orEmpty().ifBlank {
            return Result.failure(workDataOf(KEY_ERROR_MESSAGE to ERROR_MISSING_GOAL))
        }
        val roleCategory = inputData.getString(KEY_ROLE_CATEGORY).orEmpty().toBackendRoleCategory()
        if (roleCategory.isBlank()) {
            return Result.failure(workDataOf(KEY_ERROR_MESSAGE to ERROR_MISSING_ROLE_CATEGORY))
        }
        val deadlineEpochMillis = inputData.getLong(KEY_DEADLINE_EPOCH_MILLIS, 0L)
        if (deadlineEpochMillis <= 0L) {
            return Result.failure(workDataOf(KEY_ERROR_MESSAGE to ERROR_MISSING_DEADLINE))
        }
        val dailyStudyHours = inputData.getFloat(KEY_DAILY_STUDY_HOURS, 0f)
        if (dailyStudyHours <= 0f) {
            return Result.failure(workDataOf(KEY_ERROR_MESSAGE to ERROR_MISSING_DAILY_STUDY_HOURS))
        }
        val quizQuestions = inputData.getStringArray(KEY_QUIZ_QUESTIONS).orEmpty()
        val quizAnswers = inputData.getStringArray(KEY_QUIZ_ANSWERS).orEmpty()
        if (quizQuestions.size != quizAnswers.size || quizAnswers.isEmpty()) {
            return Result.failure(workDataOf(KEY_ERROR_MESSAGE to ERROR_MISSING_QUIZ_ANSWERS))
        }

        ensureNotificationChannel()
        updateProgress(goal = goal, progress = 5, stage = "")

        if (isStopped) {
            return Result.failure(workDataOf(KEY_ERROR_MESSAGE to ERROR_CANCELLED))
        }

        val result = coroutineScope {
            val roadmapDeferred = async {
                generateRoadmap(
                    request = AiRoadmapGenerationRequest(
                        draft = AiRoadmapDraft(
                            topic = goal,
                            deadlineEpochMillis = deadlineEpochMillis,
                            dailyStudyHours = dailyStudyHours,
                            roleCategory = roleCategory
                        ),
                        answers = quizQuestions.zip(quizAnswers).map { (question, answer) ->
                            AiRoadmapAnswer(
                                question = question,
                                answer = answer
                            )
                        }
                    )
                )
            }

            val progressJob = launch {
                updateProgress(goal = goal, progress = 30, stage = "")
                delay(5000)
                updateProgress(goal = goal, progress = 60, stage = "")
                delay(5000)
                updateProgress(goal = goal, progress = 90, stage = "")
            }

            val apiResult = roadmapDeferred.await()
            progressJob.cancel()
            apiResult
        }

        if (isStopped) {
            return Result.failure(workDataOf(KEY_ERROR_MESSAGE to ERROR_CANCELLED))
        }

        return when (result) {
            is NetworkResult.Success -> {
                updateProgress(goal = goal, progress = 100, stage = STAGE_READY)
                showCompletionNotification(goal)

                Result.success(
                    workDataOf(
                        KEY_ROADMAP_ID to result.data.roadmap.id,
                        KEY_PROGRESS to 100,
                        KEY_STAGE to STAGE_READY,
                        KEY_GOAL to goal
                    )
                )
            }

            is NetworkResult.Error -> {
                Result.failure(
                    workDataOf(
                        KEY_ERROR_MESSAGE to result.message,
                        KEY_PROGRESS to 30,
                        KEY_STAGE to ""
                    )
                )
            }
        }
    }

    private suspend fun generateRoadmap(
        request: AiRoadmapGenerationRequest
    ): NetworkResult<GenerateRoadmapResponseDto> {
        val cookieJar = SessionCookieJar(
            SharedPreferencesSessionCookieStorage(context.applicationContext)
        )
        val api = ApiClient.fromBuildConfig(cookieJar).createService(AiRoadmapApi::class.java)
        return SafeApiCall.execute {
            api.generateRoadmap(request.toDto())
        }
    }

    private suspend fun updateProgress(goal: String, progress: Int, stage: String) {
        setProgress(
            workDataOf(
                KEY_PROGRESS to progress,
                KEY_STAGE to stage,
                KEY_GOAL to goal
            )
        )
        setForeground(createForegroundInfo(goal = goal, progress = progress, stage = stage))
    }

    private fun createForegroundInfo(goal: String, progress: Int, stage: String): ForegroundInfo {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_rmap)
            .setContentTitle(context.getString(R.string.ai_roadmap_notification_title))
            .setContentText(context.getString(R.string.ai_roadmap_notification_body, goal, stage))
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    context.getString(R.string.ai_roadmap_notification_body, goal, stage)
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
    private fun showCompletionNotification(goal: String) {
        if (!canPostNotifications()) return

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_rmap)
            .setContentTitle(context.getString(R.string.ai_roadmap_notification_ready_title))
            .setContentText(context.getString(R.string.ai_roadmap_notification_ready_body, goal))
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    context.getString(R.string.ai_roadmap_notification_ready_body, goal)
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
        const val KEY_GOAL = "goal"
        const val KEY_ROLE_CATEGORY = "role_category"
        const val KEY_DEADLINE_EPOCH_MILLIS = "deadline_epoch_millis"
        const val KEY_DAILY_STUDY_HOURS = "daily_study_hours"
        const val KEY_QUIZ_QUESTIONS = "quiz_questions"
        const val KEY_QUIZ_ANSWERS = "quiz_answers"
        const val KEY_PROGRESS = "progress"
        const val KEY_STAGE = "stage"
        const val KEY_ROADMAP_ID = "roadmap_id"
        const val KEY_ERROR_MESSAGE = "error_message"
        const val CHANNEL_ID = "roadmap_generation"

        private const val REQUEST_CODE_OPEN_APP = 4401
        private const val ONGOING_NOTIFICATION_ID = 4501
        private const val COMPLETION_NOTIFICATION_ID = 4502
        private const val ERROR_MISSING_GOAL = "Missing roadmap goal"
        private const val ERROR_MISSING_ROLE_CATEGORY = "Missing roadmap role category"
        private const val ERROR_MISSING_DEADLINE = "Missing roadmap deadline"
        private const val ERROR_MISSING_DAILY_STUDY_HOURS = "Missing daily study hours"
        private const val ERROR_MISSING_QUIZ_ANSWERS = "Missing quiz answers"
        private const val ERROR_CANCELLED = "Roadmap generation was cancelled"
        private const val STAGE_READY = "Ready"
        private val NOTIFICATION_COLOR = Color.rgb(43, 127, 255)
    }
}
