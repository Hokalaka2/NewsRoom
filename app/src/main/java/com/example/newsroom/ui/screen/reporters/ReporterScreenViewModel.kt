package com.example.newsroom.ui.screen.reporters

import androidx.lifecycle.ViewModel
import com.example.newsroom.data.Reporter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import com.example.newsroom.ui.screen.signup.SignUpViewModel

sealed interface ReporterScreenUIState {
    object Init : ReporterScreenUIState

    data class Success(val reporterList: List<Reporter>) : ReporterScreenUIState
    data class Error(val error: String?) : ReporterScreenUIState
}

class ReporterScreenViewModel : ViewModel() {

    var currentUserId: String

    init {
        //auth = FirebaseAuth.getInstance()
        currentUserId = Firebase.auth.currentUser!!.uid
        //currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun reportersList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(SignUpViewModel.COLLECTION_REPORTERS)
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
}