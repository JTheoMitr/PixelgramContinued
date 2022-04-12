package com.gitbusters.pixelgram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Display the logo of the application
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setLogo()

        val dummyPosts = generateDummyData()
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

    /* Create the behavoir for clicking the toolbar buttons */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        // TODO create click behavoirs
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    // Sets the logo
    fun setLogo() {
        val toolbarTitle = " Pixelgram"
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.drawable.ic_pixelgram_logo)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = toolbarTitle
    }

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

// For generating some dummy data.
class DummyPost(
    val username: String,
    val commentCount: Int,
    val likeCount: Int
)