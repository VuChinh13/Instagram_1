package com.example.instagram.ui.component.myprofile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.instagram.R
import com.example.instagram.data.model.InforUserResponse
import com.example.instagram.databinding.FragmentMyProfileBinding
import com.example.instagram.ui.component.myprofile.adapter.MyPostAdapter
import com.example.instagram.ui.component.splash.SplashActivity
import com.example.instagram.ui.component.updateinformation.UpdateInformationFragment

const val EXTRA_NAME = "extra_name"
const val EXTRA_GENDER = "extra_gender"
const val EXTRA_AVATAR = "extra_avatar"
const val EXTRA_ADDRESS = "extra_address"
const val EXTRA_INTRODUCE = "extra_introduce"

class MyProfileFragment : Fragment() {
    private lateinit var binding: FragmentMyProfileBinding
    private val myProfileViewModel: MyProfileViewModel by viewModels()
    private var inforUserResponse: InforUserResponse? = null
    private lateinit var myPostAdapter: MyPostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Lấy username mà nằm trong Shared
        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("username", "").toString()

        // lấy thông tin từ trên Server
        myProfileViewModel.getInforUser(userName)
        myProfileViewModel.getUserPosts(userName)


        // chuyển sang bên UpdateInformation
        binding.ivUpdateInfor.setOnClickListener {
            // Tạo một Bundle để chứa dữ liệu
            Log.d("Check null", inforUserResponse?.data?.name.toString())
            val bundle = Bundle().apply {
                putString(EXTRA_NAME, inforUserResponse?.data?.name.toString())
                putString(EXTRA_GENDER, inforUserResponse?.data?.gender.toString())
                putString(EXTRA_AVATAR, inforUserResponse?.data?.avatar.toString())
                putString(EXTRA_INTRODUCE, inforUserResponse?.data?.introduce.toString())
                putString(EXTRA_ADDRESS, inforUserResponse?.data?.address.toString())
            }

            val updateInformationFragment = UpdateInformationFragment()
            updateInformationFragment.arguments = bundle

            // Thực hiện giao dịch
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragment, updateInformationFragment)
                .addToBackStack(null)
                .commit()
        }

        myProfileViewModel.getUserPostsResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                if (result.data.data.isEmpty()) {
                    binding.tvTitle1.visibility = View.VISIBLE
                    binding.tvTitle2.visibility = View.VISIBLE
                } else {
                    binding.tvTitle1.visibility = View.GONE
                    binding.tvTitle2.visibility = View.GONE

                    val sharedPreferences =
                        requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val userId = sharedPreferences.getString("_id", "") ?: ""
                    val userName = sharedPreferences.getString("username", "") ?: ""
                    myPostAdapter =
                        MyPostAdapter(requireActivity(), result.data.data, userName, userId)
                    binding.rvMyPost.layoutManager = LinearLayoutManager(
                        requireActivity(),
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.rvMyPost.adapter = myPostAdapter
                }
            }
        }

        myProfileViewModel.getInforUserResult.observe(viewLifecycleOwner) { result ->
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
                    requireContext(),
                    "Đã xảy ra lỗi, hãy kiểm tra lại",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }


        // sự kiện logout
        binding.ivLogout.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Dialog_Alert)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất tài khoản?")
                .setPositiveButton("Đồng ý") { dialog, _ ->
                    // Khi nhấn đăng xuất thì thực hiện đăng xuất
                    val sharedPreferences =
                        requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().clear().apply()

                    // Chuyển hướng về SplashActivity
                    val intent = Intent(requireActivity(), SplashActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    // Đóng hộp thoại và quay lại màn hình trước
                    dialog.dismiss()
                    parentFragmentManager.popBackStack()
                }
                .setNegativeButton("Hủy") { dialog, _ ->
                    // Khi mà nhấn hủy thì đóng hộp thoại
                    dialog.dismiss()
                }
            // Hiển thị hộp thoại
            alertDialog.show()
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
        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("username", "").toString()

        myProfileViewModel.getInforUser(userName)
        myProfileViewModel.getUserPosts(userName)
    }
}


