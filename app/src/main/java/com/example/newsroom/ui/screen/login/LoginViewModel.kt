package com.example.newsroom.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.newsroom.ui.screen.signup.RegisterUIState
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

sealed interface LoginUiState {
    object Init : LoginUiState
    object LoginSuccess : LoginUiState
    data class Error(val error: String?) : LoginUiState
    object Loading : LoginUiState
}

class LoginViewModel() : ViewModel() {
    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Init)
    private lateinit var auth: FirebaseAuth

    init {
        auth = Firebase.auth
    }

    suspend fun loginUser(email: String, password: String) : AuthResult? {
        loginUiState = LoginUiState.Loading

        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            loginUiState = LoginUiState.LoginSuccess

            result
        } catch (e: java.lang.Exception) {
            loginUiState = LoginUiState.Error(e.message)

            null
        }
    }

}