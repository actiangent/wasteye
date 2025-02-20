package com.actiangent.wasteye

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.actiangent.wasteye.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
                // (https://issuetracker.google.com/issues/139738913)
                finishAfterTransition()
            } else {
                handleOnBackPressed()
            }
        }
    }
}
