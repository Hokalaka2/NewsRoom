package com.example.newsroom.ui.screen.writepost

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsroom.data.Post
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.*

sealed interface WritePostUiState {
    object Init : WritePostUiState
    object LoadingPostUpload : WritePostUiState
    object PostUploadSuccess : WritePostUiState
    data class ErrorDuringPostUpload(val error: String?) : WritePostUiState
}


class WritePostViewModel: ViewModel() {
    companion object {
        const val COLLECTION_POSTS = "posts"
    }

    var writePostUiState: WritePostUiState by mutableStateOf(WritePostUiState.Init)
    private lateinit var auth: FirebaseAuth

    init {
        auth = Firebase.auth
    }

    fun uploadPost(
        title: String, postBody: String, imgUrl: String = ""
    ) {
        writePostUiState = WritePostUiState.LoadingPostUpload

        val myPost = Post(
            uid = auth.currentUser!!.uid,
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