package com.actiangent.wasteye.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.actiangent.wasteye.WasteyeObjectDetector
import com.actiangent.wasteye.databinding.FragmentCameraBinding
import com.actiangent.wasteye.model.WasteType
import com.actiangent.wasteye.view.OverlayView
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.LinkedList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val CAMERA_PERMISSION = Manifest.permission.CAMERA

class CameraFragment : Fragment(), OverlayView.OnClickListener {

    private var _fragmentCameraBinding: FragmentCameraBinding? = null
    private val fragmentCameraBinding get() = _fragmentCameraBinding!!

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Camera permission granted", Toast.LENGTH_LONG)
                    .show()

                // Wait for the views to be properly laid out
                fragmentCameraBinding.viewFinder.post {
                    // Set up the camera and its use cases
                    setUpCamera()
                }

                objectDetector =
                    WasteyeObjectDetector(
                        requireContext()
                    ) { detections, imageHeight, imageWidth ->
                        activity?.runOnUiThread {
                            fragmentCameraBinding.overlay.setResults(
                                detections ?: LinkedList<Detection>(),
                                imageHeight,
                                imageWidth
                            )

                            fragmentCameraBinding.overlay.invalidate()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_LONG)
                    .show()
            }
        }

    private lateinit var bitmapBuffer: Bitmap
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var objectDetector: WasteyeObjectDetector

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false)

        return fragmentCameraBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        fragmentCameraBinding.overlay.setOnclickListener(this)
    }

    override fun onResume() {
        super.onResume()

        if (hasPermissions(requireContext())) {
            // Wait for the views to be properly laid out
            fragmentCameraBinding.viewFinder.post {
                // Set up the camera and its use cases
                setUpCamera()
            }

            objectDetector =
                WasteyeObjectDetector(
                    requireContext()
                ) { detections, imageHeight, imageWidth ->
                    activity?.runOnUiThread {
                        fragmentCameraBinding.overlay.setResults(
                            detections ?: LinkedList<Detection>(),
                            imageHeight,
                            imageWidth
                        )

                        fragmentCameraBinding.overlay.invalidate()
                    }
                }
        } else {
            AlertDialog.Builder(requireContext())
                .apply {
                    setTitle("Permission not granted")
                    setMessage("Please grant camera permission in order to application work")
                    setNegativeButton("No") { _, _ -> }
                    setPositiveButton("Yes") { dialogInterface, i ->
                        requestPermissionLauncher.launch(CAMERA_PERMISSION)
                    }
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentCameraBinding = null

        cameraExecutor.shutdown()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation = fragmentCameraBinding.viewFinder.display.rotation
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()

                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun bindCameraUseCases() {
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        val resolutionSelector = ResolutionSelector.Builder()
            .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
            .build()
        preview =
            Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                // .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .build()

        imageAnalyzer =
            ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                // .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        if (!::bitmapBuffer.isInitialized) {
                            bitmapBuffer = Bitmap.createBitmap(
                                image.width,
                                image.height,
                                Bitmap.Config.ARGB_8888
                            )
                        }

                        detectObjects(image)
                    }
                }

        // Unbind before rebinding
        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

            preview?.surfaceProvider = fragmentCameraBinding.viewFinder.surfaceProvider
        } catch (e: Exception) {
            Log.e(TAG, "Camera use case binding failed", e)
        }
    }

    private fun detectObjects(image: ImageProxy) {
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val imageRotation = image.imageInfo.rotationDegrees
        objectDetector.detect(bitmapBuffer, imageRotation)
    }

    override fun onWasteClicked(type: WasteType) {
        val wasteBottomSheetFragment = WasteBottomSheetFragment(type)
        wasteBottomSheetFragment.show(parentFragmentManager, WasteBottomSheetFragment.TAG)
    }

    companion object {
        const val TAG = "CameraFragment"
    }
}

private fun hasPermissions(context: Context) =
    ContextCompat.checkSelfPermission(context, CAMERA_PERMISSION) ==
            PackageManager.PERMISSION_GRANTED

