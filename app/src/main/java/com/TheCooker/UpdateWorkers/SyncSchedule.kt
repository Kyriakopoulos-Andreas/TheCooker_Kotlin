package com.TheCooker.UpdateWorkers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.TheCooker.SearchToolBar.RecipeRepo.RecipeRepo
import com.TheCooker.SyncMealsWorker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Duration
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
fun scheduleMonthlySync(context: Context) {
    // Ανάθεσε την εργασία κάθε 15 λεπτά (ελάχιστο)

    val workRequest = PeriodicWorkRequestBuilder<SyncMealsWorker>(2, TimeUnit.MINUTES)
        // Εδώ μπορείς να προσθέσεις περιορισμούς αν χρειάζεται
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "SyncMealsWork",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun calculateInitialDelay(): Long {
    // Έλεγχος αν είμαστε σε κατάσταση δοκιμής
    val isTesting = true // Μπορείς να το αλλάξεις σε false όταν δεν θες να είσαι σε κατάσταση δοκιμής

    if (isTesting) {
        // Επιστρέφει 3 λεπτά σε χιλιοστά του δευτερολέπτου για δοκιμές
        return 180000 // 3 λεπτά = 180000 milliseconds
    }

    val currentDate = LocalDate.now()
    val targetDay = 24
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
