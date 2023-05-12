package com.example.newsroom.ui.screen.main

import android.app.Application
import android.util.Log
import androidx.compose.animation.core.snap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.newsroom.data.Post
import com.example.newsroom.data.PostWithId
import com.example.newsroom.data.User
import com.example.newsroom.ui.screen.login.LoginUiState
import com.example.newsroom.ui.screen.writepost.WritePostViewModel
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Document

sealed interface MainScreenUIState {
    object Init : MainScreenUIState

    data class Success(val postList: List<PostWithId>) : MainScreenUIState
    data class Error(val error: String?) : MainScreenUIState
}

sealed interface GetUserUIState {
    object Init : GetUserUIState
    data class Error(val error: String?) : GetUserUIState
    object Loading : GetUserUIState
    data class Success(val user: User): GetUserUIState

}

class MainScreenViewModel(application: Application) : ViewModel() {
    var userUIState: GetUserUIState by mutableStateOf(GetUserUIState.Init)

    var currentUserId: String

    var currentUser: User? by mutableStateOf(null)

    init {
        //auth = FirebaseAuth.getInstance()
        currentUserId = Firebase.auth.currentUser!!.uid
        getUser()
    }

   fun getUser() {
       userUIState = GetUserUIState.Loading
        FirebaseFirestore.getInstance().collection(WritePostViewModel.COLLECTION_USERS).document(currentUserId).get()
            .addOnSuccessListener() { documentSnapshot ->
                Log.e("cu", documentSnapshot.toString())

                if(documentSnapshot != null) {
                    currentUser = documentSnapshot.toObject<User>()
                    userUIState = GetUserUIState.Success(currentUser!!)
                }

            }
            .addOnFailureListener(){ e ->
                userUIState = GetUserUIState.Error(e.message)
            }
    }

    fun savePost(post: Post) {
        currentUser!!.savedPosts.add(post)
    }
    fun postsList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(WritePostViewModel.COLLECTION_POSTS)
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val postList = snapshot.toObjects(Post::class.java)
                        val postWithIdList = mutableListOf<PostWithId>()
                        postList.forEachIndexed { index, post ->
                            postWithIdList.add(PostWithId(snapshot.documents[index].id, post))
                        }
                        MainScreenUIState.Success(
                            postWithIdList
                        )
                    } else {
                        MainScreenUIState.Error(e?.message.toString())
                    }

                    trySend(response)
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

    fun deletePost(postKey: String) {
        FirebaseFirestore.getInstance().collection(
            WritePostViewModel.COLLECTION_POSTS
        ).document(postKey).delete()
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainScreenViewModel(application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])!!)
            }
        }
    }
}