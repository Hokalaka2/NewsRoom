package com.example.newsroom.data

data class Reporter(
    var uid: String = "",
    var author: String = "",
    var posts: ArrayList<Post> = ArrayList()
)