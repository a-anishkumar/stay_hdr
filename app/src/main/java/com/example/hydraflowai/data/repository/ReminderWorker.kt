package com.example.hydraflowai.data.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hydraflowai.MainActivity
import com.example.hydraflowai.HydraFlowApplication
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("ReminderWorker", "Starting hydration reminder worker...")
        
        val app = applicationContext as HydraFlowApplication
        val repository = app.repository
        
        if (!repository.isRemindersEnabled()) {
            Log.d("ReminderWorker", "Reminders are disabled.")
            return Result.success()
        }
        
        // Check time range
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val startHour = repository.getReminderStartHour()
        val endHour = repository.getReminderEndHour()
        
        // Handle normal range and overnight range
        val isWithinRange = if (startHour <= endHour) {
            currentHour in startHour..endHour
        } else {
            currentHour >= startHour || currentHour <= endHour
        }
        
        if (!isWithinRange) {
            Log.d("ReminderWorker", "Current hour $currentHour is outside reminder window [$startHour..$endHour].")
            return Result.success()
        }
        
        // Check if user already hit their goal
        val intakes = repository.getTodayIntakes().firstOrNull() ?: emptyList()
        val goal = repository.getDailyGoalFlow().firstOrNull()?.goalMl ?: repository.getOrCreateDailyGoal()
        val totalHydrated = intakes.sumOf { (it.amountMl * it.hydrationScore).toInt() }
        
        if (totalHydrated >= goal) {
            Log.d("ReminderWorker", "Hydration goal already met ($totalHydrated/$goal ml). Skipping reminder.")
            return Result.success()
        }
        
        // Show notification
        showNotification(totalHydrated, goal)
        return Result.success()
    }

    private fun showNotification(currentMl: Int, goalMl: Int) {
        val channelId = "hydration_reminders"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Periodic reminders to stay hydrated."
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val remainingMl = goalMl - currentMl
        val title = "Time to Hydrate! 💧"
        val content = "You've logged $currentMl ml of your $goalMl ml goal. Drink a glass of water to stay on track (need $remainingMl ml more)!"
        
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
            
        try {
            notificationManager.notify(42, notification)
            Log.d("ReminderWorker", "Hydration notification posted successfully.")
        } catch (e: SecurityException) {
            Log.e("ReminderWorker", "Permission error posting notification", e)
        }
    }
}
