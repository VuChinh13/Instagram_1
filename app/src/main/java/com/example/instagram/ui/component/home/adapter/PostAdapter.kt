package com.example.instagram.ui.component.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.instagram.R
import com.example.instagram.data.model.InforUserResponse
import com.example.instagram.data.model.Post
import com.example.instagram.data.repository.AuthRepository
import com.example.instagram.databinding.ItemFirstPostBinding
import com.example.instagram.databinding.ItemPostBinding
import com.example.instagram.ui.component.profile.ProfileActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

const val EXTRA_USER_NAME = "extra_user_name"

class PostAdapter(
    private val posts: List<Post>,
    private val userName: String,
    private val userId: String,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val authRepository = AuthRepository()
    private val adapterScope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_NORMAL = 0
    }

    class FirstPostViewHolder(val binding: ItemFirstPostBinding): RecyclerView.ViewHolder(binding.root){
        val imageUserStory: ImageView = binding.ivUserStory
    }

    class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        val username: TextView = binding.tvUsername
        val caption: TextView = binding.tvContent
        val viewPager: ViewPager2 = binding.viewPager
        val imageAvatar: ImageView = binding.ivAvatar
        val tvCreateAt: TextView = binding.tvCreateAt
        val tvTotalLike: TextView = binding.tvTotalLike
        val imageLike: ImageView = binding.ivLike
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_NORMAL) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post, parent, false)
            val binding = ItemPostBinding.bind(view)
            PostViewHolder(binding)
        } else { // VIEW_TYPE_HEADER
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_first_post, parent, false)
            val binding = ItemFirstPostBinding.bind(view)
            FirstPostViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var liked = false
        val post = posts[position]

        if (holder is FirstPostViewHolder){
            // trả về item đầu tiên
            // lấy được thông tin người dùng




        } else if (holder is PostViewHolder){
            // Nếu không phải item đầu tiên thì trả về item bình thường
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


            // sự kiện tym bài viết
            holder.imageLike.setOnClickListener {
                if (liked) {
                    // nếu mà đã like rồi
                    adapterScope.launch {
                        val result = authRepository.likePost(userId, post._id, -1)
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
                } else {
                    adapterScope.launch {
                        val result = authRepository.likePost(userId, post._id, 1)
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

            // Hiển thị thông tin của người dùng
            holder.imageAvatar.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra(EXTRA_USER_NAME, post.author.username)
                context.startActivity(intent)
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

    public fun updateInfor(myInfor: InforUserResponse){
        // hàm này đảm nhận việc hiển thị thông tin người dùng

    }

}
