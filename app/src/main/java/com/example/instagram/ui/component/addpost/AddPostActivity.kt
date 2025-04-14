package com.example.instagram.ui.component.addpost

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.instagram.databinding.ActivityAddPostBinding
import java.io.File
import java.io.FileOutputStream

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostBinding
    private val postImages: MutableList<File> = mutableListOf()
    private val addPostViewModel: AddPostViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
                if (uris.isNotEmpty()) {
                    uris.forEach { uri ->
                        binding.ivPostImage.setImageURI(uri)
                        postImages.add(uriToFile(this, uri))
                    }
                }
            }

        binding.btChooseImage.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.ivClose.setOnClickListener {
            finish()
        }

        addPostViewModel.getPostResponse.observe(this) { result ->
            if (result != null) {
                // Nếu mà thành công
                Toast.makeText(this, "Đã chia sẻ thành công bài viết", Toast.LENGTH_SHORT)
                    .show()
                finish()
            } else Toast.makeText(this, "Đã có lỗi xảy ra hãy kiểm tra lại", Toast.LENGTH_SHORT).show()
        }

        binding.btShare.setOnClickListener {
            if (binding.etContent.text.isEmpty()) {
                Toast.makeText(this, "Hãy nhập nội dung bài viết", Toast.LENGTH_SHORT).show()
            } else {
                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("_id", "") ?: ""
                addPostViewModel.addPost(userId, binding.etContent.text.toString(), postImages)
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }
}