package com.example.hydraflowai.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.hydraflowai.R
import com.example.hydraflowai.HydraFlowApplication
import com.example.hydraflowai.data.model.Beverage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HydraWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "com.example.hydraflowai.ACTION_QUICK_ADD") {
            val pendingResult = goAsync()
            val app = context.applicationContext as HydraFlowApplication
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    app.repository.addIntake(250, Beverage.WATER)
                    
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val thisWidget = ComponentName(context, HydraWidgetProvider::class.java)
                    val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
                    for (appWidgetId in appWidgetIds) {
                        updateAppWidget(context, appWidgetManager, appWidgetId)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.hydra_widget)

        val quickAddIntent = Intent(context, HydraWidgetProvider::class.java).apply {
            action = "com.example.hydraflowai.ACTION_QUICK_ADD"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            quickAddIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btn_quick_add, pendingIntent)

        val app = context.applicationContext as HydraFlowApplication
        CoroutineScope(Dispatchers.IO).launch {
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val intakes = app.repository.getTodayIntakes().firstOrNull() ?: emptyList()
            val totalHydrated = intakes.sumOf { (it.amountMl * it.hydrationScore).toInt() }
            val goal = app.repository.getDailyGoalFlow(todayDate).firstOrNull()?.goalMl ?: 2000
            val streak = app.repository.getStreakFlow().firstOrNull()?.currentStreak ?: 0

            views.setTextViewText(R.id.widget_progress, "Hydrated: $totalHydrated / $goal ml")
            views.setTextViewText(R.id.widget_streak, "Streak: $streak Days ??")
            
            val completion = if (goal > 0) (totalHydrated.toFloat() / goal.toFloat() * 100).toInt() else 0
            val aiTip = when {
                completion == 0 -> "Coach: Drink 300ml to start your day!"
                completion < 40 -> "Coach: You are running dry. Log a glass!"
                completion < 90 -> "Coach: Good pace, keep it up!"
                else -> "Coach: Goal met! Excellent job today."
            }
            views.setTextViewText(R.id.widget_coach_tip, aiTip)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
