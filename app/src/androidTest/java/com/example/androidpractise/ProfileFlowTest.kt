package com.example.androidpractise

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.os.Build
import android.widget.TimePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.util.Calendar
import java.util.Locale

class ProfileFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun editSaveAndRecreateProfile() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES
            else Manifest.permission.READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= 28) {
            instrumentation.uiAutomation.grantRuntimePermission(instrumentation.targetContext.packageName, permission)
        } else {
            instrumentation.uiAutomation.executeShellCommand(
                "pm grant ${instrumentation.targetContext.packageName} $permission"
            ).close()
        }
        if (Build.VERSION.SDK_INT >= 33) {
            instrumentation.uiAutomation.grantRuntimePermission(instrumentation.targetContext.packageName,
                Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= 31) {
            instrumentation.uiAutomation.executeShellCommand(
                "appops set ${instrumentation.targetContext.packageName} SCHEDULE_EXACT_ALARM allow"
            ).use { android.os.ParcelFileDescriptor.AutoCloseInputStream(it).readBytes() }
        }
        compose.onNodeWithText("Профиль").performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithContentDescription("Редактировать профиль").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription("Редактировать профиль").performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("ФИО").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("ФИО").performTextReplacement("Practice Six User")
        compose.onNodeWithText("Ссылка HTTPS на файл резюме").performTextReplacement("https://example.org/resume.pdf")
        val time = compose.onNodeWithText("Время любимой пары")
        time.performScrollTo().performTextReplacement("25:00")
        compose.onNodeWithText("Готово").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Выбрать время").performClick()
        onView(isAssignableFrom(TimePicker::class.java)).check(matches(isDisplayed()))
        onView(withId(android.R.id.button1)).perform(click())
        compose.onNodeWithText("Готово").assertIsEnabled()
        val target = Calendar.getInstance().apply {
            if (get(Calendar.HOUR_OF_DAY) != 23 || get(Calendar.MINUTE) != 59) add(Calendar.MINUTE, 1)
        }
        val classTime = String.format(Locale.ROOT, "%02d:%02d", target.get(Calendar.HOUR_OF_DAY),
            target.get(Calendar.MINUTE))
        time.performTextReplacement(classTime)
        compose.onNodeWithText("Готово").performScrollTo().performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Practice Six User").fetchSemanticsNodes().isNotEmpty() &&
                compose.onAllNodesWithText("ФИО").fetchSemanticsNodes().isEmpty()
        }
        compose.onNodeWithText("Practice Six User").assertIsDisplayed()
        compose.onNodeWithText("Время любимой пары: $classTime").assertExists()
        compose.onNodeWithText("Резюме").assertIsEnabled()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Practice Six User").fetchSemanticsNodes().isNotEmpty() &&
                compose.onAllNodesWithText("ФИО").fetchSemanticsNodes().isEmpty()
        }
        compose.onNodeWithText("Practice Six User").assertIsDisplayed()
        val manager = instrumentation.targetContext.getSystemService(NotificationManager::class.java)
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.CREATED)
        compose.waitUntil(75_000) {
            manager.activeNotifications.any {
                it.notification.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ==
                    "Practice Six User, ваша пара начинается!"
            }
        }
        manager.activeNotifications.first { it.id == 1 }.notification.contentIntent.send()
        compose.waitUntil(10_000) {
            compose.activityRule.scenario.state == androidx.lifecycle.Lifecycle.State.RESUMED
        }
    }
}
