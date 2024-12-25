package com.TheCooker.Domain.Layer.UseCase.SyncMealsFromApiToFirebaseWork

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.LocalDateTime
import java.time.Duration
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
fun scheduleMonthlySync(context: Context) {

    val workRequest = PeriodicWorkRequestBuilder<SyncMealsFromApiToFirebase>(7, TimeUnit.DAYS)
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
    // Έλεγχος αν είμαστε σε κατάσταση δοκιμής
    val isTesting = false // Αν θες να κάνεις test και να ορίσεις άλλον χρόνο άλλαξέ το σε true

    if (isTesting) {
        // Επιστρέφει 3 λεπτά σε χιλιοστά του δευτερολέπτου για δοκιμές
        return 60000// 3 λεπτά = 180000 milliseconds
    }

    val currentDateTime = LocalDateTime.now()
    val targetDayOfWeek = java.time.DayOfWeek.THURSDAY // Ορίζουμε την ημέρα της εβδομάδας που θέλουμε να εκτελείται
    val targetHour = 0
    val targetMinute = 0

    var targetDateTime = currentDateTime.withHour(targetHour).withMinute(targetMinute).withSecond(0).withNano(0)
        .with(java.time.temporal.TemporalAdjusters.nextOrSame(targetDayOfWeek))

    // Αν η τρέχουσα ημερομηνία και ώρα είναι μετά την προγραμματισμένη ώρα αυτής της εβδομάδας, πήγαινε στην επόμενη εβδομάδα
    if (currentDateTime >= targetDateTime) {
        targetDateTime = targetDateTime.plusWeeks(1)
    }

    return Duration.between(currentDateTime, targetDateTime).toMillis().coerceAtLeast(0)
}