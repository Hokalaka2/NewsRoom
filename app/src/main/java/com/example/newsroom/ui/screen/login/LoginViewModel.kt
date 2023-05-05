package com.example.newsroom.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

sealed interface LoginUiState {
    object Init : LoginUiState
    object LoginSuccess : LoginUiState
    object RegisterSuccess : LoginUiState
    data class Error(val error: String?) : LoginUiState
    object Loading : LoginUiState
}
class LoginViewModel() : ViewModel() {
    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Init)

    private lateinit var auth: FirebaseAuth

    init {
        auth = Firebase.auth
    }

    suspend fun registerUser(email: String, password: String) {
        loginUiState = LoginUiState.Loading
        delay(2000)
        try{
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                loginUiState = LoginUiState.RegisterSuccess
            }.addOnFailureListener {
                loginUiState = LoginUiState.Error(it.message)
            }
        } catch (e: java.lang.Exception){
            loginUiState = LoginUiState.Error(e.message)
        }
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