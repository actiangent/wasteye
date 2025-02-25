package com.actiangent.wasteye

import android.Manifest
import android.content.res.Configuration
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.actiangent.wasteye.fragment.CameraFragment
import com.actiangent.wasteye.model.Language
import com.actiangent.wasteye.model.Waste
import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// End-to-end tests
@RunWith(AndroidJUnit4::class)
class WasteyeTest {

    @get:Rule
    val cameraAccess = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @Test
    fun detectWasteDetail() {
        val waste = Waste.GLASS

        launchFragment<CameraFragment>(
            themeResId = R.style.Wasteye
        ).apply {
            moveToState(Lifecycle.State.RESUMED)
            onFragment { fragment ->
                fragment.onWasteClicked(waste)
            }
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        with(context.resources) {
            onView(withId(R.id.image_waste)).check(matches(isDisplayed()))
            onView(withId(R.id.text_waste_name)).apply {
                check(matches(isDisplayed()))
                check(matches(withText(getString(waste.nameResId))))
            }
        }
    }

    @Test
    fun selectWasteTypeDetail() {
        // open app
        ActivityScenario.launch(MainActivity::class.java)

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        with(context.resources) {
            // click hamburger icon button
            onView(withId(R.id.icon_button_hamburger)).perform(click())

            // check if drawer is displayed
            onView(withId(R.id.drawer)).check(matches(isDisplayed()))

            // click "Waste Types"
            onView(withText(R.string.wastes_heading)).perform(click())

            // check "Glass" waste type is displayed in list page and click it
            onView(withText(R.string.waste_type_glass)).apply {
                check(matches(isDisplayed()))
                perform(click())
            }

            // check image is displayed in detail page
            onView(withId(R.id.image_waste)).check(matches(isDisplayed()))

            // check "Glass" waste type title in detail page
            onView(withId(R.id.text_waste_name)).apply {
                check(matches(isDisplayed()))
                check(matches(withText(getString(R.string.waste_type_glass))))
            }

            // check "Glass" waste type explanation in detail page
            onView(withId(R.id.text_waste_explanation)).apply {
                check(matches(isDisplayed()))
                check(matches(withText(getString(R.string.waste_type_explanation_glass))))
            }

            // check "Glass" waste type sortation in detail page
            onView(withId(R.id.text_waste_sortation)).apply {
                check(matches(isDisplayed()))
                getStringArray(R.array.waste_type_sortation_glass).forEach { point ->
                    check(matches(withText(point)))
                }
            }

            // check "Glass" waste type recycling in detail page
            onView(withId(R.id.text_waste_recycling)).apply {
                check(matches(isDisplayed()))
                check(matches(withText(getString(R.string.waste_type_recycling_glass))))
            }
        }
    }

    @Test
    fun changeLocaleIndonesiaLanguage() {
        // open app
        ActivityScenario.launch(MainActivity::class.java)

        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        // click hamburger icon button
        onView(withId(R.id.icon_button_hamburger)).perform(click())

        // check if drawer is displayed
        onView(withId(R.id.drawer)).check(matches(isDisplayed()))

        // click "Settings"
        onView(withId(R.id.drawer_item_settings)).perform(click())

        // click "Choose Language" dropdown
        onView(withId(R.id.dropdown_language)).perform(click())

        val indonesiaLanguage = Language.BAHASA_INDONESIA
        // click "Bahasa Indonesia" from dropdown
        onData(equalTo(indonesiaLanguage)).inRoot(RootMatchers.isPlatformPopup()).perform(click())

        val indonesiaLocalizedContext =
            targetContext.createConfigurationContext(Configuration(targetContext.resources.configuration).apply {
                setLocale(indonesiaLanguage.locale)
            })
        // check after restart if use correct locale
        assertEquals(
            targetContext.resources.getString(R.string.waste),
            indonesiaLocalizedContext.resources.getString(R.string.waste)
        )
    }
}