package android.example.retrofitcoroutinesdemo.api

data class Post(
    val author: Author,
    val comments: Comments,
    val createdOn: String,
    val hasLiked: Boolean,
    val id: Int,
    val imageUrl: String,
    val likeCount: Int,
    val message: String
)
