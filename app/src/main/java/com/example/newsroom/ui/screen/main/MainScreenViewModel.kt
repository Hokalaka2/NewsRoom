package com.example.newsroom.ui.screen.main

import androidx.lifecycle.ViewModel
import com.example.newsroom.data.Post
import com.example.newsroom.data.PostWithId
import com.example.newsroom.ui.screen.writepost.WritePostViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

sealed interface MainScreenUIState {
    object Init : MainScreenUIState

    data class Success(val postList: List<PostWithId>) : MainScreenUIState
    data class Error(val error: String?) : MainScreenUIState
}

class MainScreenViewModel : ViewModel() {

    var currentUserId: String

    init {
        //auth = FirebaseAuth.getInstance()
        currentUserId = Firebase.auth.currentUser!!.uid
        //currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
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
}