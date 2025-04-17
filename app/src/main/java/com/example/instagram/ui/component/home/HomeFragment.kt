package com.example.instagram.ui.component.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagram.R
import com.example.instagram.databinding.FragmentHomeBinding
import com.example.instagram.ui.component.home.adapter.EXTRA_USER_NAME
import com.example.instagram.ui.component.home.adapter.OnAvatarClickListener
import com.example.instagram.ui.component.home.adapter.PostAdapter
import com.example.instagram.ui.component.profile.ProfileFragment

class HomeFragment : Fragment(), OnAvatarClickListener {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gọi các phương thức để lấy dữ liệu từ ViewModel
        homeViewModel.getPost()    // Lấy tất cả các bài viết
        homeViewModel.getPost1()   // Lấy thông tin người dùng

        // Quan sát dữ liệu kết hợp từ combinedData
        homeViewModel.combinedData.observe(viewLifecycleOwner, Observer { combined ->
            val posts = combined.first
            val authors = combined.second

            if (posts != null && authors != null) {
                // Cập nhật RecyclerView với các bài viết và tác giả
                val sharedPreferences =
                    requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("_id", "") ?: ""
                val userName = sharedPreferences.getString("username", "") ?: ""
                postAdapter = PostAdapter(posts.data.data, authors, userName, userId, requireContext(),this)
                binding.rvHome.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                val itemAnimator = DefaultItemAnimator().apply {
                    addDuration = 400  // Thời gian thêm item
                    removeDuration = 400  // Thời gian xóa item
                    moveDuration = 400  // Thời gian di chuyển item
                    changeDuration = 400  // Thời gian thay đổi item
                }
                binding.rvHome.itemAnimator = itemAnimator
                binding.rvHome.adapter = postAdapter
            } else {
                Toast.makeText(requireContext(), "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onAvatarClick(username: String) {
        // Khi nhấn vào avatar, thay đổi Fragment
        val profileFragment = ProfileFragment()
        val bundle = Bundle()
        bundle.putString(EXTRA_USER_NAME, username)
        profileFragment.arguments = bundle

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_right, // Hiệu ứng khi Fragment mới vào (hiện dần dần)
            R.anim.slide_out_left,
            0,
            0
        )
        transaction.add(R.id.fragment, profileFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}