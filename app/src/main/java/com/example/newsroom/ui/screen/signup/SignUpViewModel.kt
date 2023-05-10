package com.example.newsroom.ui.screen.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.newsroom.data.Post
import com.example.newsroom.data.Reporter
import com.example.newsroom.ui.screen.writepost.WritePostUiState
import com.example.newsroom.ui.screen.writepost.WritePostViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

sealed interface RegisterUIState {
    object Init : RegisterUIState
    object RegisterSuccess : RegisterUIState
    object ReporterCollectionAdded : RegisterUIState
    data class Error(val error: String?) : RegisterUIState
    object Loading : RegisterUIState
}

class RegisterViewModel(): ViewModel() {
    companion object {
        const val COLLECTION_REPORTERS = "reporters"
    }

    var registerUIState: RegisterUIState by mutableStateOf(RegisterUIState.Init)
    private lateinit var auth: FirebaseAuth

    init {
        auth = Firebase.auth
    }

    fun createReporter(
    ) {
        registerUIState = RegisterUIState.Loading

        val myReporter = Reporter(
            uid = auth.currentUser!!.uid,
            author = auth.currentUser!!.email!!,
        )

        val reporterCollection = FirebaseFirestore.getInstance().collection(COLLECTION_REPORTERS)

        reporterCollection.add(myReporter).addOnSuccessListener {
            registerUIState = RegisterUIState.ReporterCollectionAdded
        }.addOnFailureListener{
            registerUIState = RegisterUIState.Error(it.message)
        }
    }

    suspend fun registerUser(email: String, password: String) {
        registerUIState = RegisterUIState.Loading
        delay(2000)
        try{
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                registerUIState = RegisterUIState.RegisterSuccess
            }.addOnFailureListener {
                registerUIState = RegisterUIState.Error(it.message)
            }
        } catch (e: java.lang.Exception){
            registerUIState = RegisterUIState.Error(e.message)
        }
    }
}