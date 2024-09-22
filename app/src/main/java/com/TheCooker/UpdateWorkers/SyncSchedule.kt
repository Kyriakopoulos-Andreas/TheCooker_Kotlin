package com.TheCooker.UpdateWorkers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.TheCooker.SyncMealsWorker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Duration
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
fun scheduleMonthlySync(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<SyncMealsWorker>(2, TimeUnit.MINUTES)
        .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "SyncMealsWork",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun calculateInitialDelay(): Long {
    val currentDate = LocalDate.now()
    val targetDay = 23
    val targetHour = 0
    val targetMinute = 0

    val targetDate = if (currentDate.dayOfMonth > targetDay) {
        currentDate.plusMonths(1).withDayOfMonth(targetDay)
    } else {
        currentDate.withDayOfMonth(targetDay)
    }

    val targetTime = LocalTime.of(targetHour, targetMinute)
    val targetDateTime = LocalDateTime.of(targetDate, targetTime)

    return Duration.between(LocalDateTime.now(), targetDateTime).toMillis().coerceAtLeast(0)
}
