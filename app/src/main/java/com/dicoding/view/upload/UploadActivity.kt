package com.dicoding.view.upload

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.data.remote.response.StoryResponse
import com.dicoding.databinding.ActivityUplaodBinding
import com.dicoding.utils.getImageUri
import com.dicoding.utils.reduceFileImage
import com.dicoding.utils.showDialog
import com.dicoding.utils.uriToFile
import com.dicoding.view.ResultStories
import com.dicoding.view.ViewModelFactory
import com.dicoding.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUplaodBinding
    private val viewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUplaodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()

    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            binding.imagePreview.setImageURI(uri)
            viewModel.seImage(uri)
        }
    }

    private val launchIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        if (it) {
            binding.imagePreview.setImageURI(viewModel.image.value)
        } else {
            viewModel.seImage(null)
        }
    }

    private fun setupAction() {
        viewModel.image.observe(this) { image ->
            binding.imagePreview.setImageURI(image)
        }
        binding.buttonCamera.setOnClickListener { startCamera() }
        binding.buttonUpload.setOnClickListener { uploadStory() }
        binding.buttonGallery.setOnClickListener { startGallery() }

    }

    private fun startGallery() =
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

    private fun startCamera() {
        viewModel.seImage(getImageUri(this))
        launchIntentCamera.launch(viewModel.image.value as Uri)
    }


    private fun uploadStory() {
        val uri = viewModel.image.value ?: return
        lifecycleScope.launch {
            try {
                showProgress(true)

                val image = withContext(Dispatchers.IO) {
                    uriToFile(uri, this@UploadActivity).reduceFileImage()
                }
                Log.d("path", image.path)

                val desc = binding.editTextDescription.text.toString()


                viewModel.uploadStory(desc, image).observe(this@UploadActivity) { res ->
                    if (res != null) {
                        handleUploadResult(res)
                    }
                }
            } catch (e: Exception) {
                showProgress(false)
                Log.e("UploadStory", "Error during image upload", e)
                showDialog(this@UploadActivity, "Error", "Unexpected error occurred", "Retry")
            }
        }
    }

    private fun handleUploadResult(res: ResultStories<StoryResponse>) {
        when (res) {
            is ResultStories.Loading -> showProgress(true)
            is ResultStories.Error -> {
                showProgress(false)
                showDialog(this, "Error", "Error : ${res.error}", "Retry")
                Log.d("UploadStoryError", res.error)
            }

            is ResultStories.Success -> {
                showProgress(false)
                if (res.data?.error == true) {
                    showDialog(this, "Error", "Error : ${res.data.message}", "Retry")
                } else {
                    navigateToMainActivity()
                }
            }
        }
    }

    private fun showProgress(isVisible: Boolean) {
        binding.progressIndicator.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@UploadActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}