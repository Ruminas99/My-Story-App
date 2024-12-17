package com.dicoding.picodiploma.loginwithanimation.view.add

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddBinding
import com.dicoding.picodiploma.loginwithanimation.getImageUri
import com.dicoding.picodiploma.loginwithanimation.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.uriToFile
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.dicoding.picodiploma.loginwithanimation.Result
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity

class AddActivity : AppCompatActivity() {
    private val viewModel: AddViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }
    private lateinit var binding: ActivityAddBinding
    private var currentImageUri: Uri? = null
    private var isCameraRequest: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galeriButton.setOnClickListener { handleGalleryClick() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { handleUploadClick() }

        observeViewModel()
        setupObserver()
        playAnimation()
        showImage()
    }

    private fun playAnimation() {
        val description = ObjectAnimator.ofFloat(binding.descriptionEditText, View.ALPHA, 0f, 1f).setDuration(500)
        val upload = ObjectAnimator.ofFloat(binding.uploadButton, View.ALPHA, 0f, 1f).setDuration(500)
        val camera = ObjectAnimator.ofFloat(binding.cameraButton, View.ALPHA, 0f, 1f).setDuration(500)
        val galeri = ObjectAnimator.ofFloat(binding.galeriButton, View.ALPHA, 0f, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(description, upload , camera, galeri)
            start()
        }

    }

    private fun observeViewModel() {
        viewModel.currentImageUri.observe(this) { uri ->
            if (uri != null) {
                binding.addImage.setImageURI(uri)
            } else {
                binding.addImage.setImageResource(R.drawable.baseline_error_outline_24)
            }
        }

        viewModel.uploadResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, "Story diunggah: ${result.data.message}", Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                is Result.Loading -> Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObserver() {
        viewModel.uploadResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("Upload Status")
                        setMessage(result.data.message)
                        setPositiveButton("OK") { _, _ ->
                            val intent = Intent(this@AddActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
                is Result.Error -> {
                    val errorMessage = result.message.ifBlank { "An unknown error occurred" }
                    AlertDialog.Builder(this).apply {
                        setTitle("Upload Failed")
                        setMessage(errorMessage)
                        setPositiveButton("OK", null)
                        create()
                        show()
                    }
                }
                is Result.Loading -> {
                    Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleGalleryClick() {
        if (allPermissionsGranted()) {
            startGallery()
        } else {
            isCameraRequest = false
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private fun handleUploadClick() {
        val description = binding.descriptionEditText.text.toString()
        val imageUri = viewModel.currentImageUri.value

        if (!validateInput(description, imageUri)) return

        val file = uriToFile(imageUri!!, this).reduceFileImage()
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("photo", file.name, requestBody)

        viewModel.uploadStory(part, description)
    }

    private fun validateInput(description: String, imageUri: Uri?): Boolean {
        return when {
            description.isBlank() -> {
                Toast.makeText(this, "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show()
                false
            }
            imageUri == null -> {
                Toast.makeText(this, "Pilih salah 1 gambar", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                if (isCameraRequest) {
                    startCamera()
                }
                else {
                    startGallery()
                }
            }
            else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun startGallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setImageUri(uri)
            showImage()

        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }
    private fun startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            currentImageUri = getImageUri(this)
            viewModel.setImageUri(currentImageUri)
            launcherIntentCamera.launch(currentImageUri!!)
        } else {
            isCameraRequest = true
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
            viewModel.setImageUri(null)
            Log.d("AddStoryActivity", "Camera capture canceled or failed.")
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

    private fun showImage() {
        val imageUri = viewModel.currentImageUri.value
        if (imageUri != null) {
            binding.addImage.setImageURI(imageUri)
            ObjectAnimator.ofFloat(binding.addImage, View.ALPHA, 0f, 1f).apply {
                duration = 500
                start()
            }
        } else {
            Log.d("AddStoryActivity", "No image URI available to display")
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}