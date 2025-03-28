package com.actiangent.wasteye

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.RectF
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.util.Consumer
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
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
import junit.framework.Assert.assertTrue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.detector.Detection
import java.io.InputStream
import java.security.Permission
import java.util.concurrent.Executors

// End-to-end tests
@RunWith(AndroidJUnit4::class)
class WasteyeTest {

    private val controlResults = listOf(
        Detection.create(
            RectF(701.0f, 259.0f, 1017.0f, 765.0f),
            listOf<Category>(Category.create("metal", "metal", 0.91550297f))
        ),
        Detection.create(
            RectF(930.0f, 523.0f, 1314.0f, 1074.0f),
            listOf<Category>(Category.create("metal", "metal", 0.7755628f))
        ),
        Detection.create(
            RectF(493.0f, 510.0f, 874.0f, 1070.0f),
            listOf<Category>(Category.create("metal", "metal", 0.7653055f))
        ),
    )

    @get:Rule val cameraAccess = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @Test
    fun permissionNotGranted() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertTrue(context.checkCallingOrSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
    }

    @Test
    fun permissionGranted_detectWaste_Incorrect() {
        // targetContext is app's context to grab assets that are in src/main/assets
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertTrue(context.checkCallingOrSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)

        // open app
        ActivityScenario.launch(MainActivity::class.java)

        val wasteyeObjectDetector =
            WasteyeObjectDetector(context) { results, _, _ ->

                // Verify result size is not the same
                ViewMatchers.assertThat(controlResults.size, IsNot.not(equalTo(results!!.size)))

                // Loop through the detected and control data
                for (i in results.indices) {
                    // Verify that the bounding boxes are the same
                    ViewMatchers.assertThat(
                        results[i].boundingBox,
                        IsNot.not(equalTo(controlResults[i].boundingBox))
                    )

                    // Verify that the detected data and control
                    // data have the same number of categories
                    assertEquals(
                        results[i].categories.size,
                        controlResults[i].categories.size
                    )

                    // Loop through the categories
                    for (j in 0 until controlResults[i].categories.size - 1) {
                        // Verify that the labels are incorrect
                        ViewMatchers.assertThat(
                            results[i].categories[j].label,
                            IsNot.not(equalTo(controlResults[i].categories[j].label))
                        )
                    }
                }
            }

        // Create Bitmap
        val bitmap = loadImage("wastes-incorrect.jpg")
        // Run the object detector on the sample image
        wasteyeObjectDetector.detect(bitmap, 0)
    }

    @Test
    fun permissionGranted_detectWaste_Correct() {
        // targetContext is app's context to grab assets that are in src/main/assets
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertTrue(context.checkCallingOrSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)

        // open app
        ActivityScenario.launch(MainActivity::class.java)

        val wasteyeObjectDetector =
            WasteyeObjectDetector(context) { results, _, _ ->

                // Verify result size is the same
                assertEquals(controlResults.size, results!!.size)

                // Loop through the detected and control data
                for (i in results.indices) {
                    // Verify that the bounding boxes are the same
                    assertEquals(results[i].boundingBox, controlResults[i].boundingBox)

                    // Verify that the detected data and control
                    // data have the same number of categories
                    assertEquals(
                        results[i].categories.size,
                        controlResults[i].categories.size
                    )

                    // Loop through the categories
                    for (j in 0 until controlResults[i].categories.size - 1) {
                        // Verify that the labels are consistent
                        assertEquals(
                            results[i].categories[j].label,
                            controlResults[i].categories[j].label
                        )
                    }
                }
            }

        // Create Bitmap
        val bitmap = loadImage("wastes.jpg")
        // Run the object detector on the sample image
        wasteyeObjectDetector.detect(bitmap, 0)
    }

    @Test
    fun permissionGranted_selectWasteTypeDetail() {
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

    private fun loadImage(fileName: String): Bitmap {
        // context is test app's context to grab assets that are in src/androidTest/assets
        val assetManager: AssetManager =
            InstrumentationRegistry.getInstrumentation().context.assets
        println("loadImage: ${assetManager.locales.size}")
        val inputStream: InputStream = assetManager.open(fileName)
        return BitmapFactory.decodeStream(inputStream)
    }
}