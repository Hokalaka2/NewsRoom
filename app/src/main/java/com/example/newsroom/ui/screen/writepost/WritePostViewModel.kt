package com.example.newsroom.ui.screen.writepost


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.newsroom.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

sealed interface WritePostUiState {
    object Init : WritePostUiState
    object LoadingPostUpload : WritePostUiState
    object PostUploadSuccess : WritePostUiState
    data class ErrorDuringPostUpload(val error: String?) : WritePostUiState
}


class WritePostViewModel: ViewModel() {
    companion object {
        const val COLLECTION_USERS = "users"
        const val COLLECTION_POSTS = "posts"
    }

    var writePostUiState: WritePostUiState by mutableStateOf(WritePostUiState.Init)
    private lateinit var auth: FirebaseAuth

    init {
        auth = Firebase.auth
    }

    fun uploadPost(
        title: String, postBody: String
    ) {
        writePostUiState = WritePostUiState.LoadingPostUpload

        val myPost = Post(
            authoruid = auth.currentUser!!.uid,
            author = auth.currentUser!!.email!!,
            title = title,
            body = postBody
        )

        val postCollection = FirebaseFirestore.getInstance().collection(COLLECTION_POSTS)

        postCollection.add(myPost).addOnSuccessListener {
            writePostUiState = WritePostUiState.PostUploadSuccess
        }.addOnFailureListener{
            writePostUiState = WritePostUiState.ErrorDuringPostUpload(it.message)
        }
    }
}