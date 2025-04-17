package com.example.instagram.ui.component.profile.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.instagram.R
import com.example.instagram.data.model.Post
import com.example.instagram.data.repository.AuthRepository
import com.example.instagram.ui.component.home.adapter.EXTRA_USER_NAME
import com.example.instagram.ui.component.home.adapter.ImagePagerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ProfileAdapter(
    private val context: Context,
    private val posts: List<Post>,
    private val userName: String,
    private val userId: String
) :
    RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {
    private val authRepository = AuthRepository()
    private val adapterScope = CoroutineScope(Dispatchers.IO)

    class ProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.tv_username)
        val caption: TextView = view.findViewById(R.id.tv_content)
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val imageAvatar: ImageView = view.findViewById(R.id.iv_avatar)
        val tvCreateAt: TextView = view.findViewById(R.id.tv_createAt)
        val tvTotalLike: TextView = view.findViewById(R.id.tv_total_like)
        val imageLike: ImageView = view.findViewById(R.id.iv_like)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        var liked = false
        val post = posts[position]
        // kiểm tra xem bài viết đã được tym hay chưa
        holder.imageLike.setImageResource(R.drawable.ic_heart)
        post.listLike.forEach { userLike ->
            if (userLike.username == userName) {
                holder.imageLike.setImageResource(R.drawable.ic_heart_red)
                liked = true
            }
        }
        holder.username.text = post.author.username
        holder.caption.text = post.content
        holder.tvCreateAt.text = formatCreatedAt(post.createdAt)
        Glide.with(holder.itemView.context).load(post.author.avatar)
            .error(R.drawable.ic_avatar)
            .into(holder.imageAvatar)
        holder.viewPager.adapter = ImagePagerAdapter(post.images)
        holder.tvTotalLike.text = post.totalLike.toString()

        holder.imageLike.setOnClickListener {
            // Sự kiện tym và bỏ tym
            if (liked) {
                // nếu mà đã like rồi
                adapterScope.launch {
                    val result = authRepository.likePost(userId, post._id, -1)
                    withContext(Dispatchers.Main) {
                        if (result != null) {
                            liked = !liked // Cập nhật lại trạng thái đã like hay chưa
                            holder.imageLike.setImageResource(R.drawable.ic_heart)
                            post.totalLike -= 1
                            holder.tvTotalLike.text = post.totalLike.toString()
                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                adapterScope.launch {
                    val result = authRepository.likePost(userId, post._id, 1)
                    withContext(Dispatchers.Main) {
                        if (result != null) {
                            liked = !liked // Cập nhật lại trạng thái đã like hay chưa
                            holder.imageLike.setImageResource(R.drawable.ic_heart_red)
                            post.totalLike += 1
                            holder.tvTotalLike.text = post.totalLike.toString()
                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun formatCreatedAt(dateString: String): String {
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)

        // Chuyển đổi từ chuỗi sang LocalDateTime
        val dateTime = LocalDateTime.parse(dateString, inputFormatter)
        return dateTime.format(outputFormatter)
    }

    override fun getItemCount(): Int = posts.size
}