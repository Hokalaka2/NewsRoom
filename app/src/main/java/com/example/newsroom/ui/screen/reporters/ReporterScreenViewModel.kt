package com.example.newsroom.ui.screen.reporters

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.newsroom.data.Follower
import com.example.newsroom.data.Reporter
import com.example.newsroom.ui.screen.signup.RegisterUIState
import com.example.newsroom.ui.screen.signup.RegisterViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

sealed interface ReporterScreenUIState {
    object Init : ReporterScreenUIState

    data class Success(val reporterList: List<Reporter>) : ReporterScreenUIState

    object FollowerCollectionAdded : ReporterScreenUIState
    data class Error(val error: String?) : ReporterScreenUIState

    object Loading : ReporterScreenUIState
}

class ReporterScreenViewModel : ViewModel() {

    var currentUserId: String

    companion object {
        const val COLLECTION_FOLLOWING = "following"
    }

    var reporterScreenUIState: ReporterScreenUIState by mutableStateOf(ReporterScreenUIState.Init)
    init {
        //auth = FirebaseAuth.getInstance()
        currentUserId = Firebase.auth.currentUser!!.uid
        //currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun reportersList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(RegisterViewModel.COLLECTION_REPORTERS)
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val reporterList = snapshot.toObjects(Reporter::class.java)
                        ReporterScreenUIState.Success(
                            reporterList
                        )
                    } else {
                        ReporterScreenUIState.Error(e?.message.toString())
                    }

                    trySend(response)
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

    suspend fun createFollower(uid: String, name: String, email: String) {
        reporterScreenUIState = ReporterScreenUIState.Loading

        val myFollower = Follower(
            uid = uid,
            name = name,
            email = email,
        )

        val followerCollection = FirebaseFirestore.getInstance().collection(RegisterViewModel.COLLECTION_USERS).document(currentUserId).collection(
            COLLECTION_FOLLOWING)

        followerCollection.document(uid).set(myFollower).addOnSuccessListener {
            reporterScreenUIState = ReporterScreenUIState.FollowerCollectionAdded
        }.addOnFailureListener{
            reporterScreenUIState = ReporterScreenUIState.Error(it.message)
        }
    }
}