package com.gitbusters.pixelgram

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gitbusters.pixelgram.api.Post

// Build the recyclerview with post_items
class PostRecyclerAdapter (private val postData: List<Post>) : RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // Post information
        val username : TextView = view.findViewById(R.id.username)
        val likeCount : TextView = view.findViewById(R.id.tv_like_count)
        val commentCount : TextView = view.findViewById(R.id.tv_comment_count)
        val postImage : ImageView = view.findViewById(R.id.iv_post_image)
        val pfpImage : ImageView = view.findViewById(R.id.iv_profilePic)
        val commentList : LinearLayout = view.findViewById(R.id.ll_comment_list)

        /* Define post click listeners below */
        init {}
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.post_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents within the view, in this case the post information.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        // Set the contents of the view.
        // Get the current post
        val post = postData[position]
        holder.username.text = post.author.username
        holder.likeCount.text = post.likeCount.toString() + " Likes"
        holder.commentCount.text = post.likeCount.toString() + " Comments"

        loadImages(post.imageUrl, post.author.profileImageUrl, context, holder)

    }
    // Maybe make this asynchronous?  I'm not sure right now
    fun loadImages(postUrl: String, pfpUrl: String, context: Context, holder: ViewHolder) {
        Glide.with(context)
            .load(postUrl)
            .error(R.drawable.ic_baseline_broken_image_24)
            .into(holder.postImage)
        Glide.with(context)
            .load(pfpUrl)
            .into(holder.pfpImage)
    }

    override fun getItemCount() = postData.size
}