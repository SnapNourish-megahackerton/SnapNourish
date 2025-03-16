package com.cs407.snapnourish

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : ComponentActivity() {


    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private val imageUploader = ImageUploader()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

//        val email = "testuser@example.com" // tester email
//        val password = "testpw"      // tester password
//
//        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d("CameraActivity", "Forced login successful")
//                    Toast.makeText(this, "Forced login successful", Toast.LENGTH_SHORT).show()
//                } else {
//                    Log.e("CameraActivity", "Forced login failed: ${task.exception?.message}")
//                    Toast.makeText(this, "Forced login failed", Toast.LENGTH_SHORT).show()
//                }
//            }

        setContentView(R.layout.activity_camera)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // ProgressBar to show upload progress
        progressBar = findViewById(R.id.uploadProgressBar)
        progressBar.visibility = ProgressBar.INVISIBLE // initially hidden

        val previewView = findViewById<PreviewView>(R.id.viewFinder)
        startCamera(previewView)

        findViewById<Button>(R.id.captureButton).setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Fail open Camera: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Check if the user is logged in
        if (FirebaseAuth.getInstance().currentUser == null) {
            Log.e("CameraActivity", "User not logged in!")
            Toast.makeText(this, "Please log in before uploading.", Toast.LENGTH_SHORT).show()
            return // Stop the photo-taking process
        }

        val photoFile = File(
            externalMediaDirs.firstOrNull(),
            "captured_image_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d("CameraActivity", "Image saved: $savedUri")

                    // show progressbar when uploading photo
                    progressBar.visibility = ProgressBar.VISIBLE
                    uploadPhotoToFirebase(savedUri)

//                    val intent = Intent(this@CameraActivity, TransitionActivity::class.java)
//                    intent.putExtra("IMAGE_URI", savedUri)
//                    startActivity(intent)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Image capture failed: ${exception.message}")
                }
            }
        )
    }

    // helper method to call uploadPhotoAndSaveUrl from imageUploader
    private fun uploadPhotoToFirebase(fileUri: Uri) {
        val isRecipe = intent.getBooleanExtra("isRecipe", false)
        imageUploader.uploadPhotoAndSaveUrl(fileUri, isRecipe) { success ->
            progressBar.visibility = ProgressBar.INVISIBLE // hide ProgressBar after upload
            if (success) {
                Log.d("CameraActivity", "Photo uploaded to Firebase successfully")
                Toast.makeText(this, "Photo uploaded successfully!", Toast.LENGTH_SHORT).show()

                // Navigate to the appropriate TransitionActivity based on isRecipe flag
                val intent = if (isRecipe) {
                    Intent(this@CameraActivity, TransitionActivityforR::class.java) // For Recipe
                } else {
                    Intent(this@CameraActivity, TransitionActivityforN::class.java) // For Nutrition
                }

                // Pass the captured image URI to the next activity
                intent.putExtra("IMAGE_URI", fileUri.toString())
                startActivity(intent)

            } else {
                Log.e("CameraActivity", "Photo upload to Firebase failed")
                Toast.makeText(this, "Failed to upload photo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

