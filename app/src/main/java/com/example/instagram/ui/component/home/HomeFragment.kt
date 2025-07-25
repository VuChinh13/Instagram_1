package com.example.instagram.ui.component.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagram.R
import com.example.instagram.databinding.FragmentHomeBinding
import com.example.instagram.ui.component.animation.FragmentTransactionAnimation.setSlideAnimations
import com.example.instagram.ui.component.home.adapter.OnAvatarClickListener
import com.example.instagram.ui.component.home.adapter.PostAdapter
import com.example.instagram.ui.component.profile.ProfileFragment
import com.example.instagram.ui.component.utils.IntentExtras
import com.example.instagram.ui.component.utils.SharedPrefer

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
        homeViewModel.getPost()   
        homeViewModel.getPost1()  

        homeViewModel.combinedData.observe(viewLifecycleOwner, Observer { combined ->
            val posts = combined.first
            val authors = combined.second

            if (posts != null && authors != null) {
                SharedPrefer.updateContext(requireContext())
                val userId = SharedPrefer.getUserId()
                val userName = SharedPrefer.getUserName()
                val userAvatar = SharedPrefer.getAvatar()
                postAdapter = PostAdapter(posts.data.data, authors, userName, userId, requireContext(),this,userAvatar)
                binding.rvHome.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                val itemAnimator = DefaultItemAnimator().apply {
                    addDuration = 400  
                    removeDuration = 400  
                    moveDuration = 400  
                    changeDuration = 400  
                }
                binding.rvHome.itemAnimator = itemAnimator
                binding.rvHome.adapter = postAdapter
            } else {
                Toast.makeText(requireContext(), "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onAvatarClick(username: String) {
        val profileFragment = ProfileFragment()
        val bundle = Bundle()
        bundle.putString(IntentExtras.EXTRA_USER_NAME, username)
        profileFragment.arguments = bundle

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setSlideAnimations()
        transaction.add(R.id.fragment, profileFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
