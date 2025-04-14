package com.example.instagram.ui.component.home
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagram.databinding.FragmentHomeBinding
import com.example.instagram.ui.component.home.adapter.PostAdapter


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getPost()
        homeViewModel.getPostResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                // Trong Fragment thì không thể gọi trực tiếp getSharedPreferences mà thông qua Activity
                val sharedPreferences =
                    requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("_id", "") ?: ""
                val userName = sharedPreferences.getString("username", "") ?: ""
                postAdapter = PostAdapter(result.data.data, userName, userId, requireActivity())
                binding.rvHome.layoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                binding.rvHome.adapter = postAdapter
            } else {
                Toast.makeText(requireActivity(), "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show()
            }
        }

    }
}