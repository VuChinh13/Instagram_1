package com.example.instagram.ui.component.home

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagram.databinding.ActivityHomeBinding
import com.example.instagram.ui.component.addpost.AddPostActivity
import com.example.instagram.ui.component.home.adapter.PostAdapter
import com.example.instagram.ui.component.myprofile.MyProfileActivity

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        homeViewModel.getPost()

        binding.ivHome.setOnClickListener {
            homeViewModel.getPost()
        }

        binding.ivProfile.setOnClickListener {
            val intent = Intent(this, MyProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        binding.ivAddPost.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
        }

        homeViewModel.getPostResult.observe(this) { result ->
        if (result != null) {
            binding.srlData.isRefreshing = false
            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("_id", "") ?: ""
            val userName = sharedPreferences.getString("username", "") ?: ""
            postAdapter = PostAdapter(result.data.data, userName, userId, this)
            binding.rvHome.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.rvHome.adapter = postAdapter
        } else {
            Toast.makeText(this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show()
        }
    }

    binding.srlData.setOnRefreshListener {
        homeViewModel.getPost()
    }
}


override fun onBackPressed() {
    // Kiểm tra nếu danh sách đã cuộn thì về đầu, nếu không thì thoát activity
    if ((binding.rvHome.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 0) {
        binding.rvHome.scrollToPosition(0)
    } else {
        super.onBackPressed() // Nếu đã ở đầu danh sách thì thoát activity
    }
}
}