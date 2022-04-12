package android.example.retrofitcoroutinesdemo.api

data class Comments(
    val author: String,
    val createdOn: String,
    val id: Int,
    val message: String,
    val postId: Int
)
