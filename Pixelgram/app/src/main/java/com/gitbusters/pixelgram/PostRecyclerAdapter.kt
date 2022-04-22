package com.gitbusters.pixelgram

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.gitbusters.pixelgram.api.Post
import android.graphics.Bitmap
import android.text.SpannableStringBuilder
import android.widget.*
import androidx.annotation.Nullable
import androidx.core.text.bold
import androidx.core.view.size
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception


// Build the recyclerview with post_items
class PostRecyclerAdapter (private var postData: List<Post>) : RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder>() {

    fun setPostData(postData: List<Post>){
        val newData : List<Post> = this.postData.plus(postData)
        this.postData = newData
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Post information
        // Change this to view binding later.
        val username : TextView = view.findViewById(R.id.username)
        val likeCount : TextView = view.findViewById(R.id.tv_like_count)
        val commentCount : TextView = view.findViewById(R.id.tv_comment_count)
        val postImage : ImageView = view.findViewById(R.id.iv_post_image)
        val pfpImage : ImageView = view.findViewById(R.id.iv_profilePic)
        val postDesc : TextView = view.findViewById(R.id.tv_author_desc)
        val viewMoreBtn : TextView = view.findViewById(R.id.tv_view_more_comments)
        val commentList : LinearLayout = view.findViewById(R.id.ll_comment_list)

        /* Define post click listeners below */
        init {
            // Viewing more comments
        }

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
        //try { // Load the post
            //Log.d("POSTURL", post.imageUrl)
            loadImages(post, context, holder)
            holder.username.text = post.author.username
            holder.likeCount.text = post.likeCount.toString() + " Likes"

            val description = SpannableStringBuilder()
                .bold{ append(post.author.username) }
                .append(" " + post.message)
            holder.postDesc.text = description
            getComments(context, holder, post)
        /*}
        catch (e: Exception) {
            Log.d("ERR", e.toString())
        }*/


        // Click listener for comments, a bit messy but functional.  For a larger scale, implement an interface.
        holder.viewMoreBtn.setOnClickListener {
            // Toast.makeText(context, post.id.toString(), Toast.LENGTH_SHORT).show()
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("postid", post.id)
            context.startActivity(intent)

        }

    }

    /* Load the first 5 comments of the post */
    /*
    private fun loadComments(content: List<Content>, context: Context, holder: ViewHolder) {
        if(holder.commentList.size < 5) {
            for (c in content) {
                val comment = SpannableStringBuilder()
                    .bold{ append(c.author.username) }
                    .append(" " + c.message)
                // Progrimatically create a textview
                val textView = TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = comment
                }
                holder.commentList.addView(textView)
            }
        }
    }
     */

    private fun getComments(context: Context, holder: ViewHolder, post: Post) {
        //BACK_END: Building our retrofit Builder instance
        val pn = 0
        val ps = 5 // Show the first 5 comments

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        MainScope().launch(Dispatchers.IO) {
            try {
                val response = api.getComments(post.id,pn,ps).awaitResponse()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    Log.d(ContentValues.TAG,data.content.toString())

                    withContext(Dispatchers.Main) {
                        // Iterate through the comments.
                        holder.commentCount.text = data.totalElements.toString() + " Comments"
                        if(holder.commentList.size < 5) {
                            for (c in data.content) {
                                val comment = SpannableStringBuilder()
                                    .bold{ append(c.author.username) }
                                    .append(" " + c.message)
                                // Progrimatically create a textview
                                val textView = TextView(context).apply {
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    text = comment
                                }
                                holder.commentList.addView(textView)
                            }
                        }
                    }
                }
            }
            //BACK_END: Handling call errors
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error loading comments", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Maybe make this asynchronous?  I'm not sure right now
    private fun loadImages(post: Post, context: Context, holder: ViewHolder){
    // Load the profile picture
        Glide.with(context)
            .load(post.author.profileImageUrl)
            .into(holder.pfpImage)
    // Load the post image
        Glide.with(context)
            .load(post.imageUrl)
            .error(R.drawable.ic_baseline_broken_image_24)
            /*.listener(object : RequestListener<Drawable?> {
                // Handle image load errors.
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // log exception
                    Log.e("IMGLOAD", "Error loading image", e)
                    return false // important to return false so the error placeholder can be placed
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })*/
            .into(holder.postImage)
    }



    override fun getItemCount() = postData.size
}