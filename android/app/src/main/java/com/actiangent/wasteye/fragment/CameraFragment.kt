package com.actiangent.wasteye.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
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
import androidx.camera.core.TorchState
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.actiangent.wasteye.R
import com.actiangent.wasteye.WasteyeApplication
import com.actiangent.wasteye.WasteyeObjectDetector
import com.actiangent.wasteye.databinding.FragmentCameraBinding
import com.actiangent.wasteye.factory.WasteyeViewModelFactory
import com.actiangent.wasteye.model.Waste
import com.actiangent.wasteye.view.OverlayView
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.LinkedList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val CAMERA_PERMISSION = Manifest.permission.CAMERA

class CameraFragment : Fragment(), OverlayView.OnClickListener {

    private var _fragmentCameraBinding: FragmentCameraBinding? = null
    private val fragmentCameraBinding get() = _fragmentCameraBinding!!

    private val viewModel: CameraViewModel by viewModels(factoryProducer = {
        WasteyeViewModelFactory(WasteyeApplication.injection)
    })

    private var isTorchEnabled = false

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

        // Initialize background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        fragmentCameraBinding.apply {
            overlay.setOnclickListener(this@CameraFragment)
            iconButtonFlash.setOnClickListener {
                camera?.cameraControl?.enableTorch(!isTorchEnabled)
            }

            iconButtonHamburger.setOnClickListener {
                drawer.openDrawer(GravityCompat.START)
            }

            drawerNavigation.setNavigationItemSelectedListener { menu ->
                when (menu.itemId) {
                    R.id.drawer_item_wastes -> {
                        navigateToWasteList()
                        true
                    }

                    R.id.drawer_item_recycle -> {
                        startMapsIntent()
                        true
                    }

                    R.id.drawer_item_settings -> {
                        navigateToSettings()
                        true
                    }

                    else -> false
                }
            }
        }

        lifecycleScope.launch {
            viewModel.userData.collect { userData ->
                fragmentCameraBinding.overlay
                    .setIsShowScore(userData.isShowDetectionScore)
            }
        }
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
            .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
            .build()

        preview =
            Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .build()

        imageAnalyzer =
            ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
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
                .also { camera ->
                    camera.cameraInfo.torchState.observe(this) { state ->
                        isTorchEnabled = (state == TorchState.ON)

                        if (isTorchEnabled) {
                            fragmentCameraBinding.iconButtonFlash.load(R.drawable.flash_on_24)
                        } else {
                            fragmentCameraBinding.iconButtonFlash.load(R.drawable.flash_off_24)
                        }
                    }
                }

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

    override fun onWasteClicked(type: Waste) {
        val wasteBottomSheetFragment = WasteBottomSheetFragment(type)
        wasteBottomSheetFragment.show(parentFragmentManager, WasteBottomSheetFragment.TAG)
    }

    private fun navigateToWasteList() {
        val action = CameraFragmentDirections.actionCameraFragmentToWasteListFragment()
        findNavController().navigate(action)
    }

    private fun navigateToSettings() {
        val action = CameraFragmentDirections.actionCameraFragmentToSettingsFragment()
        findNavController().navigate(action)
    }

    private fun startMapsIntent() {
        val query = getString(R.string.nearest_recycle_centre).lowercase()
        val mapsIntentUri = Uri.parse("geo:0,0?q=$query")
        val mapsIntent = Intent(Intent.ACTION_VIEW, mapsIntentUri)
        mapsIntent.setPackage("com.google.android.apps.maps")
        requireContext().startActivity(mapsIntent)
    }

    override fun onDestroy() {
        super.onDestroy()

        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()

        _fragmentCameraBinding = null
    }

    companion object {
        const val TAG = "CameraFragment"
    }
}

private fun hasPermissions(context: Context) =
    ContextCompat.checkSelfPermission(context, CAMERA_PERMISSION) ==
            PackageManager.PERMISSION_GRANTED

