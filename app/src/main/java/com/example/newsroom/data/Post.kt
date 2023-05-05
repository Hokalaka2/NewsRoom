package com.example.newsroom.data

data class Post(
    var uid: String = "",
    var author: String = "",
    var title: String = "",
    var body: String = "",
)

data class PostWithId(
    val postId: String,
    val post: Post
)