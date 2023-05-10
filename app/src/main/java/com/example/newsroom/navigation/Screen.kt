package com.example.newsroom.navigation

sealed class Screen(val route: String) {
    object LoginUser : Screen("login")
    object Main : Screen("main")
    object WritePost : Screen("writepost")
    object RegisterUser : Screen("register")

    object Reporters : Screen("reporters")
}