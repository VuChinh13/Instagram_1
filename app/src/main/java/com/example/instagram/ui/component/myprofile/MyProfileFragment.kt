package com.example.instagram.ui.component.myprofile

import android.annotation.SuppressLint
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
import com.example.instagram.ui.component.main.MainActivity
import com.example.instagram.ui.component.myprofile.adapter.MyPostAdapter
import com.example.instagram.ui.component.splash.SplashActivity
import com.example.instagram.ui.component.updateinformation.UpdateInformationFragment
import com.example.instagram.ui.component.utils.IntentExtras
import com.example.instagram.ui.component.utils.SharedPrefer

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

    @SuppressLint("UseKtx")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).fragmentCurrent = "MyProfileFragment"
        (activity as MainActivity).countFragment ++

        SharedPrefer.updateContext(requireContext())
        val userName = SharedPrefer.getUserName()
        myProfileViewModel.getInforUser(userName)
        myProfileViewModel.getUserPosts(userName)


        binding.ivUpdateInfor.setOnClickListener {
            Log.d("Check null", inforUserResponse?.data?.name.toString())
            val bundle = Bundle().apply {
                putString(IntentExtras.EXTRA_NAME, inforUserResponse?.data?.name.toString())
                putString(IntentExtras.EXTRA_GENDER, inforUserResponse?.data?.gender.toString())
                putString(IntentExtras.EXTRA_AVATAR, inforUserResponse?.data?.avatar.toString())
                putString(
                    IntentExtras.EXTRA_INTRODUCE,
                    inforUserResponse?.data?.introduce.toString()
                )
                putString(IntentExtras.EXTRA_ADDRESS, inforUserResponse?.data?.address.toString())
            }

            val updateInformationFragment = UpdateInformationFragment()
            updateInformationFragment.arguments = bundle

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

                    SharedPrefer.updateContext(requireContext())
                    val userId = SharedPrefer.getUserId()
                    val userName = SharedPrefer.getUserName()
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

        binding.ivLogout.setOnClickListener {
            val alertDialog =
                AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Dialog_Alert)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất tài khoản?")
                    .setPositiveButton("Đồng ý") { dialog, _ ->
                        // Clear dữ liệu
                        SharedPrefer.getSharedPrefer().edit().clear().apply()
                        val intent = Intent(requireActivity(), SplashActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        dialog.dismiss()
                        parentFragmentManager.popBackStack()
                    }
                    .setNegativeButton("Hủy") { dialog, _ ->
                        dialog.dismiss()
                    }
            alertDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        SharedPrefer.updateContext(requireContext())
        val userName = SharedPrefer.getUserName()
        myProfileViewModel.getInforUser(userName)
        myProfileViewModel.getUserPosts(userName)
    }
}


