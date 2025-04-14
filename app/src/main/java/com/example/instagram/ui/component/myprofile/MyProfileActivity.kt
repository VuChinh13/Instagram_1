package com.example.instagram.ui.component.myprofile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.instagram.R
import com.example.instagram.data.model.InforUserResponse
import com.example.instagram.databinding.ActivityMyProfileBinding
import com.example.instagram.ui.component.addpost.AddPostActivity
import com.example.instagram.ui.component.home.HomeActivity
import com.example.instagram.ui.component.myprofile.adapter.MyPostAdapter
import com.example.instagram.ui.component.splash.SplashActivity
import com.example.instagram.ui.component.updateinformation.UpdateInformationActivity

const val EXTRA_NAME = "extra_name"
const val EXTRA_GENDER = "extra_gender"
const val EXTRA_AVATAR = "extra_avatar"
const val EXTRA_ADDRESS = "extra_address"
const val EXTRA_INTRODUCE = "extra_introduce"


class MyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private val myProfileViewModel: MyProfileViewModel by viewModels()
    private var inforUserResponse: InforUserResponse? = null
    private lateinit var myPostAdapter: MyPostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy username mà nằm trong Shared
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("username", "").toString()

        // lấy thông tin từ trên Server
        myProfileViewModel.getInforUser(userName)
        myProfileViewModel.getUserPosts(userName)

        binding.ivHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        binding.ivAddPost.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
        }

        binding.ivProfile.setOnClickListener {
            finish()
            startActivity(Intent(this, MyProfileActivity::class.java))
        }

        // chuyển sang bên UpdateInformation
        binding.ivUpdateInfor.setOnClickListener {
            val intent = Intent(this, UpdateInformationActivity::class.java).apply {
                putExtra(EXTRA_NAME, inforUserResponse?.data?.name)
                putExtra(EXTRA_GENDER, inforUserResponse?.data?.gender)
                putExtra(EXTRA_AVATAR, inforUserResponse?.data?.avatar)
                putExtra(EXTRA_INTRODUCE, inforUserResponse?.data?.introduce)
                putExtra(EXTRA_ADDRESS, inforUserResponse?.data?.address)
            }
            startActivity(intent)
        }

        myProfileViewModel.getUserPostsResult.observe(this) { result ->
            if (result != null) {
                if (result.data.data.isEmpty()) {
                    binding.tvTitle1.visibility = View.VISIBLE
                    binding.tvTitle2.visibility = View.VISIBLE
                } else {
                    binding.tvTitle1.visibility = View.GONE
                    binding.tvTitle2.visibility = View.GONE

                    val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val userId = sharedPreferences.getString("_id", "") ?: ""
                    val userName = sharedPreferences.getString("username", "") ?: ""
                    myPostAdapter = MyPostAdapter(this, result.data.data, userName, userId)
                    binding.rvMyPost.layoutManager = LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.rvMyPost.adapter = myPostAdapter
                }
            }
        }

        myProfileViewModel.getInforUserResult.observe(this) { result ->
            if (result != null) {
                inforUserResponse = result
                Glide.with(this).load(result.data.avatar)
                    .error(R.drawable.ic_avatar)
                    .into(binding.ivAvatar)
                binding.tvName.text = result.data.name
                binding.tvUsername.text = result.data.username
                binding.tvTotalPost.text = result.data.totalPost.toString()
                binding.tvUsernameThread.text = result.data.username
                binding.tvIntroduce.text = result.data.introduce
            } else {
                Toast.makeText(
                    this,
                    "Đã xảy ra lỗi, hãy kiểm tra lại",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }


        // sự kiện logout
        binding.ivLogout.setOnClickListener {
            showConfirmationDialog(this, "Bạn có chắc chắn muốn đăng xuất tài khoản?") {
                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                val intent = Intent(this, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showConfirmationDialog(context: Context, message: String, onYes: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("Đăng xuất")
            .setMessage(message)
            .setPositiveButton("Xác nhận") { dialog, _ ->
                onYes()
                dialog.dismiss()
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Trường hợp khi mà quay lại thì cập nhật lại dữ liệu mới nhất thì lưu vào trong shared
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("username", "").toString()

        myProfileViewModel.getInforUser(userName)
        myProfileViewModel.getUserPosts(userName)
    }

}