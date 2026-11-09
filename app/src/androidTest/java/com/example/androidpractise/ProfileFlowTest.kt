package com.example.androidpractise

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.androidpractise.items.ui.ItemsActivity
import org.junit.Rule
import org.junit.Test

class ProfileFlowTest {
    @get:Rule val compose = createAndroidComposeRule<ItemsActivity>()

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
        compose.onNodeWithText("Готово").performScrollTo().performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Practice Six User").fetchSemanticsNodes().isNotEmpty() &&
                compose.onAllNodesWithText("ФИО").fetchSemanticsNodes().isEmpty()
        }
        compose.onNodeWithText("Practice Six User").assertIsDisplayed()
        compose.onNodeWithText("Резюме").assertIsEnabled()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Practice Six User").fetchSemanticsNodes().isNotEmpty() &&
                compose.onAllNodesWithText("ФИО").fetchSemanticsNodes().isEmpty()
        }
        compose.onNodeWithText("Practice Six User").assertIsDisplayed()
    }
}
