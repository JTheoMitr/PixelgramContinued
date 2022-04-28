package com.gitbusters.pixelgram

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitbusters.pixelgram.api.CommentObject
import com.gitbusters.pixelgram.api.Comments
import com.gitbusters.pixelgram.api.MainObject
import com.gitbusters.pixelgram.api.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject constructor(
    private val repository : PostsRepo
) : ViewModel() {
    private val _response_posts = MutableLiveData<MainObject>()
    var posts : LiveData<MainObject>
    private val _response_comments = MutableLiveData<CommentObject>()
    var comments:LiveData<CommentObject>
init{
    posts = _response_posts
    comments = _response_comments
}

    //val repo = PostsRepo()

//    val posts : MutableLiveData<List<Post>> by lazy {
//        MutableLiveData<List<Post>>().also {
//            loadPosts()
//        }
//    }

    fun returnPosts(pageNumber:Int, pageSize: Int): LiveData<MainObject> {
        getPosts(pageNumber,pageSize)
        return posts
    }


    fun getPosts(pageNumber:Int, pageSize: Int) = viewModelScope.launch {
        repository.getPosts(pageNumber, pageSize).let{response->
            if(response.isSuccessful){
                _response_posts.postValue(response.body())
            }
            else{
                //...............
            }
        }
        posts = _response_posts
    }

    fun returnComments(postId:Int,pageNumber:Int, pageSize: Int): LiveData<CommentObject> {
        getComments(postId,pageNumber,pageSize)
        return comments
    }


    fun getComments(postId: Int,pageNumber:Int, pageSize: Int) = viewModelScope.launch {
        repository.getComments(postId,pageNumber, pageSize).let{response->
            if(response.isSuccessful){
                _response_comments.postValue(response.body())
            }
            else{
                //...............
            }
        }
        comments = _response_comments
    }

}
