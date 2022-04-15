package com.gitbusters.pixelgram

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gitbusters.pixelgram.api.CommentObject

class CommentRecyclerAdapter(private val data: CommentObject) : RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.tv_comment_username)
        val body: TextView = view.findViewById(R.id.tv_comment_body)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = data.content[position]
        holder.username.text = comment.author.username
        holder.body.text = comment.message
    }

    override fun getItemCount(): Int = data.content.size

}