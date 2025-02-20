package com.actiangent.wasteye.model

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.actiangent.wasteye.WasteyeObjectDetector
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.detector.Detection
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class WasteyeModelTest {

    private val controlResults = listOf(
        Detection.create(
            RectF(54.0f, 642.0f, 660.0f, 1520.0f),
            listOf<Category>(Category.create("plastic", "plastic", 0.73046875f))
        ),
        Detection.create(
            RectF(803.0f, 1083.0f, 1511.0f, 1682.0f),
            listOf<Category>(Category.create("plastic", "plastic", 0.65625f))
        ),
        Detection.create(
            RectF(10.0f, 221.0f, 645.0f, 1671.0f),
            listOf<Category>(Category.create("plastic", "plastic", 0.6171875f))
        ),
    )

    @Test
    fun model_isDetectedResultsCorrect() {
        // targetContext is app's context to grab assets that are in src/main/assets
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val wasteyeObjectDetector =
            WasteyeObjectDetector(context) { results, _, _ ->

                // Verify result size is the same
                assertEquals(controlResults.size, results!!.size)

                // Loop through the detected and control data
                for (i in controlResults.indices) {
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
        val bitmap = loadImage("plastics.jpg")
        // Run the object detector on the sample image
        wasteyeObjectDetector.detect(bitmap, 0)
    }

    @Test
    fun model_isDetectedImageScaledWithinModelDimens() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val wasteyeObjectDetector =
            WasteyeObjectDetector(context) { results, imageHeight, imageWidth ->

                assertNotNull(results)

                for (result in results!!) {
                    assertTrue(result.boundingBox.top <= imageHeight)
                    assertTrue(result.boundingBox.bottom <= imageHeight)
                    assertTrue(result.boundingBox.left <= imageWidth)
                    assertTrue(result.boundingBox.right <= imageWidth)
                }
            }

        // Create Bitmap
        val bitmap = loadImage("plastics.jpg")
        // Run the object detector on the sample image
        wasteyeObjectDetector.detect(bitmap, 0)
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