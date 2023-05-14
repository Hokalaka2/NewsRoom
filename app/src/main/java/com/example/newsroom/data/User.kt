package com.example.newsroom.data

data class User(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var reporter: Boolean = false,
    var following: List<Follower> = emptyList(),
    var savedPosts: ArrayList<Post> = ArrayList()
)