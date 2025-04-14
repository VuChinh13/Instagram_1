package com.example.instagram.ui.component.updateinformation

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.instagram.R
import com.example.instagram.databinding.ActivityUpdateInformationBinding
import com.example.instagram.ui.component.myprofile.EXTRA_ADDRESS
import com.example.instagram.ui.component.myprofile.EXTRA_AVATAR
import com.example.instagram.ui.component.myprofile.EXTRA_GENDER
import com.example.instagram.ui.component.myprofile.EXTRA_INTRODUCE
import com.example.instagram.ui.component.myprofile.EXTRA_NAME
import java.io.File
import java.io.FileOutputStream

class UpdateInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateInformationBinding
    private var selectedImageUri: Uri? = null
    private val updateInformationViewModel: UpdateInformationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name: String = intent.getStringExtra(EXTRA_NAME) ?: ""
        val introduce: String = intent.getStringExtra(EXTRA_INTRODUCE) ?: ""
        val gender: String = intent.getStringExtra(EXTRA_GENDER) ?: "Other"
        val avatar: String = intent.getStringExtra(EXTRA_AVATAR) ?: ""
        val address: String = intent.getStringExtra(EXTRA_ADDRESS) ?: ""


        binding.etName.append(name)
        binding.etIntroduce.append(introduce)
        binding.etAddress.append(address)

        if (avatar.isEmpty()) {
            binding.ivAvatar.setImageResource(R.drawable.ic_avatar)
        } else Glide.with(this).load(avatar).into(binding.ivAvatar)

        // trường hợp nếu mà giới tính là null thì để là khác
        val items = listOf("Nam", "Nữ", "Khác")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
        if (gender.equals("Khác")) {
            binding.spinnerGender.setSelection(2)
        } else if (gender.equals("Nam")) {
            binding.spinnerGender.setSelection(0)

        } else binding.spinnerGender.setSelection(1)

        // Lấy dữ liệu trong SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("_id", "") ?: ""

        updateInformationViewModel.updateInforResult.observe(this) { result ->
            if (result != null) {
                // Thay đổi thông tin người dùng thành công
                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("username", result.data.username)
                editor.putString("password", result.data.password)
                editor.putString("name", result.data.name)
                editor.putString("avatar", result.data.avatar)
                editor.putString("gender", result.data.gender)
                editor.putString("address", result.data.address)
                editor.putString("introduce", result.data.introduce)
                Toast.makeText(
                    this,
                    "Updated information successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    " Error, please check the information again!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let {
                    selectedImageUri = it // Lưu Uri vào biến
                    binding.ivAvatar.setImageURI(it) // Hiển thị ảnh trên ImageView
                }
            }

        // Khi mà nhấn Avatar thì chọn ảnh từ thư viện
        binding.ivAvatar.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.ivBackArrow.setOnClickListener {
            finish()
        }
        binding.btSaveChanges.setOnClickListener {
                val avatarFile = selectedImageUri?.let {
                    uriToFile(
                        this,
                        it
                    )
                } // Chuyển uri thành File nếu mà có ảnh mới
                if (binding.etOldPassword.text.toString().isEmpty() && binding.etNewPassword.text.toString().isEmpty()){
                    // nếu mà 2 trường này mà trống thì truyền null
                    updateInformationViewModel.updateInformation(
                       null,
                        null,
                        binding.etName.text.toString().trim(),
                        avatarFile,
                        binding.spinnerGender.selectedItem.toString().trim(),
                        binding.etAddress.text.toString().trim(),
                        binding.etIntroduce.text.toString().trim(),
                        userId
                    )
                } else {
                    updateInformationViewModel.updateInformation(
                        binding.etOldPassword.text.toString().trim(),
                        binding.etNewPassword.text.toString().trim(),
                        binding.etName.text.toString().trim(),
                        avatarFile,
                        binding.spinnerGender.selectedItem.toString().trim(),
                        binding.etAddress.text.toString().trim(),
                        binding.etIntroduce.text.toString().trim(),
                        userId
                    )
                }
            }
        }


    // Hàm dùng để chuyển uri thành 1 đối tượng là File
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