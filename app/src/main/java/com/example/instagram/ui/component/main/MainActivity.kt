package com.example.instagram.ui.component.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instagram.R
import com.example.instagram.databinding.ActivityMainBinding
import com.example.instagram.ui.component.addpost.AddPostFragment
import com.example.instagram.ui.component.home.HomeFragment
import com.example.instagram.ui.component.myprofile.MyProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val transactionHomeFragment = supportFragmentManager.beginTransaction()
        transactionHomeFragment.add(R.id.fragment,homeFragment)
        transactionHomeFragment.commit()

        binding.btnAdd.setOnClickListener {
            val addPostFragment = AddPostFragment()
            val transactionAddPostFragment = supportFragmentManager.beginTransaction()
            transactionAddPostFragment.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,0,0)
            transactionAddPostFragment.add(R.id.fragment,addPostFragment)
            transactionAddPostFragment.addToBackStack(null)
            transactionAddPostFragment.commit()
        }

        binding.btnMyProfile.setOnClickListener {
            val myProfileFragment = MyProfileFragment()
            val transactionMyProfileFragment = supportFragmentManager.beginTransaction()
            transactionMyProfileFragment.setCustomAnimations(
                R.anim.slide_in_right, // Hiệu ứng khi Fragment mới vào (hiện dần dần)
                R.anim.slide_out_left,
                0,
                0
            )
            transactionMyProfileFragment.add(R.id.fragment,myProfileFragment)
            transactionMyProfileFragment.addToBackStack(null)
            transactionMyProfileFragment.commit()
        }

    }
}