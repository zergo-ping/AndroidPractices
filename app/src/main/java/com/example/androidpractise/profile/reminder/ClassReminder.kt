package com.example.androidpractise.profile.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.androidpractise.R
import com.example.androidpractise.items.ui.ItemsActivity
import java.util.Calendar

object ClassReminder {
    const val CHANNEL = "class_reminders"
    const val OWNER = "profile_owner"
    private const val FLAGS = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

    fun today(time: String): Long {
        val now = Calendar.getInstance()
        val trigger = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, time.substringBefore(':').toInt())
            set(Calendar.MINUTE, time.substringAfter(':').toInt())
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        require(now.timeInMillis - trigger.timeInMillis < 60_000) {
            "Это время сегодня уже прошло. Выберите более позднее время"
        }
        return trigger.timeInMillis
    }

    fun canSchedule(context: Context) = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
        context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()

    fun prepareChannel(context: Context): Boolean {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(NotificationChannel(CHANNEL, "Начало пары", NotificationManager.IMPORTANCE_HIGH))
            if (manager.getNotificationChannel(CHANNEL).importance == NotificationManager.IMPORTANCE_NONE) return false
        }
        return manager.areNotificationsEnabled()
    }

    fun schedule(context: Context, trigger: Long, name: String) {
        val intent = Intent(context, ClassReminderReceiver::class.java).putExtra(OWNER, name)
        val alarm = PendingIntent.getBroadcast(context, 0, intent, FLAGS)
        context.getSystemService(AlarmManager::class.java)
            .setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, alarm)
    }
}

class ClassReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val owner = intent.getStringExtra(ClassReminder.OWNER) ?: return
        if (!ClassReminder.prepareChannel(context)) return
        val open = Intent(context, ItemsActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val notification = NotificationCompat.Builder(context, ClassReminder.CHANNEL)
            .setSmallIcon(R.drawable.ic_class_clock)
            .setContentTitle("Начало любимой пары")
            .setContentText(if (owner.isBlank()) "Ваша пара начинается!" else "$owner, ваша пара начинается!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(PendingIntent.getActivity(context, 0, open, PendingIntent.FLAG_IMMUTABLE))
            .setAutoCancel(true)
            .build()
        context.getSystemService(NotificationManager::class.java).notify(1, notification)
    }
}
