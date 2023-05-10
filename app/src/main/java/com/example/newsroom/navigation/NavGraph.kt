package com.example.newsroom

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newsroom.navigation.Screen
import com.example.newsroom.ui.screen.login.LoginScreen
import com.example.newsroom.ui.screen.main.MainScreen
import com.example.newsroom.ui.screen.reporters.ReporterScreen
import com.example.newsroom.ui.screen.writepost.WritePostScreen
import com.example.newsroom.ui.screen.signup.SignUpScreen

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.LoginUser.route
    ) {
        composable(Screen.LoginUser.route) {
            LoginScreen(
                onLoginSuccess = {
                navController.navigate(Screen.Main.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.RegisterUser.route)
                }
            )
        }
        composable(Screen.Main.route) {
            MainScreen(
                onWriteNewPostClick = {
                    navController.navigate(Screen.WritePost.route)
                },
                onReportersClick = {
                    navController.navigate(Screen.Reporters.route)
                }
            )
        }
        composable(Screen.WritePost.route) {
            WritePostScreen(
                onWritePostSuccess = {
                    //navController.navigate(Screen.Main.route)
                    navController.popBackStack(Screen.Main.route, false)
                }
            )
        }
        composable(Screen.RegisterUser.route){
            SignUpScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Main.route)
                }
            )
        }
        composable(Screen.Reporters.route){
            ReporterScreen()
        }
    }
}