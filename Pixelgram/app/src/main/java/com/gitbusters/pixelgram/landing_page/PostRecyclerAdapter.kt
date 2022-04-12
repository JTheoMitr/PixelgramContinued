package com.gitbusters.pixelgram.landing_page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gitbusters.pixelgram.R

// Build the recyclerview with post_items
class PostRecyclerAdapter (private val postData: ArrayList<DummyPost>) : RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // Post information
        val username : TextView = view.findViewById(R.id.username)
        val likeCount : TextView = view.findViewById(R.id.tv_like_count)
        val commentCount : TextView = view.findViewById(R.id.tv_comment_count)
        val postImage : ImageView = view.findViewById(R.id.iv_post_image)
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
        // Set the contents of the view.
        // Get the current post
        val post = postData[position]
        holder.username.text = post.username
        holder.likeCount.text = post.likeCount.toString() + " Likes"
        holder.commentCount.text = post.commentCount.toString() + " Comments"

    }

    override fun getItemCount() = postData.size
}