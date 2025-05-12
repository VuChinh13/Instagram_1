package com.example.instagram.ui.component.signup

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.instagram.R
import com.example.instagram.databinding.ActivitySignupBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivBackArrow.setOnClickListener {
            finish()
        }
        signupViewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })
        signupViewModel.signResult.observe(this) { result ->
            if (result != null) {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                // Nếu mà đăng kí thành công thì chuyển về AuthActivity
                if (result.message.equals("Tạo tài khoản thành công!")) {
                    lifecycleScope.launch {
                        delay(2000)
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Đã xảy ra lỗi hãy kiểm tra lại", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btSignup.setOnClickListener {
            signupViewModel.signup(
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString(),
                binding.etName.text.toString()
            )
        }
    }
}
