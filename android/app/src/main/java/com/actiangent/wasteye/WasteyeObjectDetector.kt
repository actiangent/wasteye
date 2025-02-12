package com.actiangent.wasteye

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class WasteyeObjectDetector(
    private val context: Context,
    private val onDetection: (
        results: MutableList<Detection>?,
        imageHeight: Int,
        imageWidth: Int
    ) -> Unit,
) {

    private val TAG = "ObjectDetector"

    private val objectDetector: ObjectDetector? by lazy { initObjectDetector() }

    private fun initObjectDetector(): ObjectDetector? {
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .apply {
                setScoreThreshold(0.5f)
                setMaxResults(3)

                val baseOptions = BaseOptions.builder()
                    .setNumThreads(2)
                    .also { builder ->
                        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                            builder.useGpu()
                        }
                    }

                setBaseOptions(baseOptions.build())
            }.build()

        return try {
            ObjectDetector.createFromFileAndOptions(context, MODEL_PATH, options)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "TFLite failed to load model with error: " + e.message)
            null
        }
    }

    fun detect(image: Bitmap, imageRotation: Int) {
        objectDetector?.let {
            val imageProcessor =
                ImageProcessor.Builder()
                    .add(Rot90Op(-imageRotation / 90))
                    .build()

            val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

            val results = objectDetector?.detect(tensorImage)
            onDetection.invoke(
                results,
                tensorImage.height,
                tensorImage.width,
            )
        }
    }

    companion object {
        private const val MODEL_PATH = "model/wasteye_quantized_metadata.tflite"
    }
}