package com.example.instagram.ui.component.auth

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.instagram.R
import com.example.instagram.databinding.ActivityAuthBinding
import com.example.instagram.ui.component.home.HomeActivity
import com.example.instagram.ui.component.signup.SignupActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Chuyển sang bên Activity tạo tài khoản
        binding.btSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        // Sự kiện nhấn nút login
        binding.btLogin.setOnClickListener {
            authViewModel.login(
                binding.etUserName.text.toString().trim(),
                binding.etPassword.text.toString().trim()
            )
        }

        authViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Lắng nghe khi mà có sự thay đổi trong dữ liệu trả về từ Server thì cập nhật thông báo
        authViewModel.loginResult.observe(this) { result ->
            if (result != null) {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                // lưu thông tin đăng nhập vào trong SharedPreferences
                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("_id", result.data._id)
                editor.putString("username", result.data.username)
                editor.putString("password", result.data.password)
                editor.putString("name", result.data.name)
                editor.putString("avatar", result.data.avatar)
                editor.putString("gender", result.data.gender)
                editor.putString("address", result.data.address)
                editor.putString("introduce", result.data.introduce)
                editor.apply()

                lifecycleScope.launch {
                    delay(1000)
                    startActivity(Intent(this@AuthActivity, HomeActivity::class.java))
                    finish() // Đóng SplashActivity
                }
            } else {
                Toast.makeText(this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show()
            }
        }
    }
}