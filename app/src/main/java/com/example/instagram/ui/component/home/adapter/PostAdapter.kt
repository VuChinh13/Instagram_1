package com.example.instagram.ui.component.home.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.instagram.R
import com.example.instagram.data.model.Author
import com.example.instagram.data.model.Post
import com.example.instagram.data.repository.AuthRepository
import com.example.instagram.databinding.ItemFirstPostBinding
import com.example.instagram.databinding.ItemPostBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

const val EXTRA_USER_NAME = "extra_user_name"

class PostAdapter(
    private val posts: List<Post>,
    private val authors: MutableList<Author>,
    private val userName: String,
    private val userId: String,
    private val context: Context,
    private val listener: OnAvatarClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val authRepository = AuthRepository()
    private val adapterScope = CoroutineScope(Dispatchers.Main)
    private var check = false

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_NORMAL = 1
    }

    class FirstPostViewHolder(val binding: ItemFirstPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageUserStory: ImageView = binding.ivUserStory
        val story: LinearLayout = binding.story
        val username: TextView = binding.tvUsername
        val caption: TextView = binding.tvContent
        val viewPager: ViewPager2 = binding.viewPager
        val imageAvatar: ImageView = binding.ivAvatar
        val tvCreateAt: TextView = binding.tvCreateAt
        val tvTotalLike: TextView = binding.tvTotalLike
        val imageLike: ImageView = binding.ivLike
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

    override fun getItemId(position: Int): Long {
        return if (position == 0) {
            0L 
        } else {
            posts[position - 1]._id.hashCode().toLong() 
        }
    }

    init {
        setHasStableIds(true) 
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
        if (holder is FirstPostViewHolder && check == false) {
            check = true
            Log.d("KT", authors.size.toString())
            authors.forEach { author ->
                val storyItemView =
                    LayoutInflater.from(context).inflate(R.layout.story_item, holder.story, false)
                val imageView: ImageView =
                    storyItemView.findViewById(R.id.iv_user_story)  
                Glide.with(context)
                    .load(author.avatar)
                    .error(R.drawable.ic_avatar)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView)

                val textView: TextView =
                    storyItemView.findViewById(R.id.textView2)  
                textView.text = author.username
                holder.story.addView(storyItemView)

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
                    if (liked) {
                        // nếu mà đã like rồi
                        adapterScope.launch {
                            val result = authRepository.likePost(userId, post._id, -1)
                            if (result != null) {
                                liked = !liked
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
                                liked = !liked
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
                holder.imageAvatar.setOnClickListener {
                    listener.onAvatarClick(post.author.username) 
                }
            }
        } else if (holder is PostViewHolder) {
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
                if (liked) {
                    // nếu mà đã like rồi
                    adapterScope.launch {
                        val result = authRepository.likePost(userId, post._id, -1)
                        if (result != null) {
                            liked = !liked 
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
                            liked = !liked
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
            holder.imageAvatar.setOnClickListener {
                listener.onAvatarClick(post.author.username) 
            }
        }
    }

    private fun formatCreatedAt(dateString: String): String {
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)
        val dateTime = LocalDateTime.parse(dateString, inputFormatter)
        return dateTime.format(outputFormatter)
    }

    override fun getItemCount(): Int = posts.size

}

interface OnAvatarClickListener {
    fun onAvatarClick(username: String)
}
