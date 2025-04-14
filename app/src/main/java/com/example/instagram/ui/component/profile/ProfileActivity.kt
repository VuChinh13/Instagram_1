package com.example.instagram.ui.component.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.instagram.R
import com.example.instagram.databinding.ActivityProfileBinding
import com.example.instagram.ui.component.home.adapter.EXTRA_USER_NAME
import com.example.instagram.ui.component.profile.adapter.ProfileAdapter


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val myProfileViewModel: ProfileViewModel by viewModels()
    private lateinit var postAdapter: ProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName: String = intent.getStringExtra(EXTRA_USER_NAME) ?: ""

        // lấy thông tin từ trên Server
        myProfileViewModel.getInforUser(userName)
        myProfileViewModel.getUserPosts(userName)


        myProfileViewModel.getUserPostsResult.observe(this) { result ->
            if (result != null) {
                if (result.data.data.isEmpty()) {
                    binding.tvTitle1.visibility = View.VISIBLE
                    binding.ivCamera.visibility = View.VISIBLE
                } else {
                    binding.tvTitle1.visibility = View.GONE
                    binding.ivCamera.visibility = View.GONE

                    val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val userId = sharedPreferences.getString("_id", "") ?: ""
                    val myUserName = sharedPreferences.getString("username", "") ?: ""

                    postAdapter = ProfileAdapter(this, result.data.data, myUserName, userId)
                    binding.rvPost.layoutManager = LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.rvPost.adapter = postAdapter
                }
            }
        }

        myProfileViewModel.getInforUserResult.observe(this) { result ->
            if (result != null) {
                Glide.with(this).load(result.data.avatar)
                    .error(R.drawable.ic_avatar)
                    .into(binding.ivAvatar)
                binding.tvName.text = result.data.name
                binding.tvUsername.text = result.data.username
                binding.tvTotalPost.text = result.data.totalPost.toString()
                binding.tvIntroduce.text = result.data.introduce
                binding.tvUsernameThread.text = result.data.username
            } else {
                Toast.makeText(
                    this,
                    "Đã xảy ra lỗi, hãy kiểm tra lại",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        binding.ivBackArrow.setOnClickListener {
            finish()
        }
    }
}