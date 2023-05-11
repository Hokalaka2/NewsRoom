package com.example.newsroom.data

data class User(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var reporter: Boolean = false,
    var following: List<Reporter> = emptyList(),
    var savedPosts: List<Post> = emptyList()
)