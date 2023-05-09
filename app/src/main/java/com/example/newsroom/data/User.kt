package com.example.newsroom.data

data class User (
    var uid: String,
    var name: String,
    var email: String,
    var reporter: Boolean,
    var following: List<Reporter>,
    var savedPosts: List<Post>
    )