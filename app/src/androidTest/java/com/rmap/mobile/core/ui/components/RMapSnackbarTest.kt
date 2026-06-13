package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SnackbarData
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rmap.mobile.R
import com.rmap.mobile.core.notification.AppNotificationVariant
import com.rmap.mobile.core.notification.AppSnackbarVisuals
import com.rmap.mobile.core.ui.theme.RMapTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RMapSnackbarTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersAllSemanticVariants() {
        composeRule.setContent {
            RMapTheme(darkTheme = false, dynamicColor = false) {
                Column {
                    AppNotificationVariant.entries.forEach { variant ->
                        RMapSnackbar(
                            snackbarData = FakeSnackbarData(
                                visuals = AppSnackbarVisuals(
                                    title = variant.name,
                                    message = "${variant.name} message",
                                    variant = variant
                                )
                            )
                        )
                    }
                }
            }
        }

        AppNotificationVariant.entries.forEach { variant ->
            composeRule.onNodeWithText(variant.name).assertIsDisplayed()
            composeRule.onNodeWithText("${variant.name} message").assertIsDisplayed()
        }
    }

    @Test
    fun actionAndDismissInvokeSnackbarCallbacks() {
        val snackbarData = FakeSnackbarData(
            visuals = AppSnackbarVisuals(
                title = "Attention needed",
                message = "Sign in to continue.",
                variant = AppNotificationVariant.Warning,
                actionLabel = "Login"
            )
        )
        val dismissDescription = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .getString(R.string.snackbar_dismiss_content_description)

        composeRule.setContent {
            RMapTheme(darkTheme = true, dynamicColor = false) {
                RMapSnackbar(snackbarData = snackbarData)
            }
        }

        composeRule.onNodeWithText("Login", ignoreCase = true).performClick()
        composeRule.onNodeWithContentDescription(dismissDescription).performClick()

        assertTrue(snackbarData.actionPerformed)
        assertTrue(snackbarData.dismissed)
    }

    @Test
    fun longMessageRemainsVisible() {
        val longMessage = "Sign in to generate your personalized roadmap and keep your learning progress synchronized across devices."

        composeRule.setContent {
            RMapTheme(darkTheme = false, dynamicColor = false) {
                RMapSnackbar(
                    snackbarData = FakeSnackbarData(
                        visuals = AppSnackbarVisuals(
                            title = "Sign in required",
                            message = longMessage,
                            variant = AppNotificationVariant.Warning,
                            actionLabel = "Login"
                        )
                    )
                )
            }
        }

        composeRule.onNodeWithText(longMessage).assertIsDisplayed()
        composeRule.onNodeWithText("Login", ignoreCase = true).assertIsDisplayed()
    }

    private class FakeSnackbarData(
        override val visuals: AppSnackbarVisuals
    ) : SnackbarData {
        var actionPerformed = false
        var dismissed = false

        override fun performAction() {
            actionPerformed = true
        }

        override fun dismiss() {
            dismissed = true
        }
    }
}
