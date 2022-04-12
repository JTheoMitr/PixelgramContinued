package com.gitbusters.pixelgram.landing_page

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gitbusters.pixelgram.R

class LandingPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Display the logo of the application
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setLogo()
        // Set up the recyclerview
        /* DUMMY DATA */ val dummyPosts = generateDummyData()
        val recyclerView = findViewById<RecyclerView>(R.id.rv_post_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PostRecyclerAdapter(dummyPosts)
        recyclerView.adapter = adapter

    }

    /* Create the buttons on the toolbar */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    /* Create the behavior for clicking the toolbar buttons */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        // Skeleton functions for toolbar actions
        R.id.action_new_post -> {
            Toast.makeText(this, "New Post Button press", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.action_profile -> {
            Toast.makeText(this, "account button pressed", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /* Set the app logo on the toolbar */
    fun setLogo() {
        val toolbarTitle = " Pixelgram"
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.drawable.ic_pixelgram_logo)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = toolbarTitle
    }

    /* Dummy Data generation */
    fun generateDummyData() : ArrayList<DummyPost> {
        val post1 = DummyPost("Ryan", 10, 20)
        val post2 = DummyPost("Ayman", 13, 66)
        val post3 = DummyPost("Tyler", 1, 123)

        val list = arrayListOf<DummyPost>()
        list.add(post1)
        list.add(post2)
        list.add(post3)
        return list
    }
}

/* Dummy Data generation */
class DummyPost(
    val username: String,
    val commentCount: Int,
    val likeCount: Int
)