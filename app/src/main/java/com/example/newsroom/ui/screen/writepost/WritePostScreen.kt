package com.example.newsroom.ui.screen.writepost

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun WritePostScreen(
    onWritePostSuccess: () -> Unit = {},
    writePostViewModel: WritePostViewModel = viewModel()
) {
    var postTitle by remember { mutableStateOf("") }
    var postBody by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        OutlinedTextField(value = postTitle,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Post title") },
            onValueChange = {
                postTitle = it
            }
        )
        OutlinedTextField(value = postBody,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Post body") },
            onValueChange = {
                postBody = it
            }
        )

        Button(onClick = {
            writePostViewModel.uploadPost(postTitle, postBody)
        }) {
            Text(text = "Upload")
        }
        when (writePostViewModel.writePostUiState) {
            is WritePostUiState.LoadingPostUpload -> CircularProgressIndicator()
            is WritePostUiState.PostUploadSuccess -> {
                Text(text = "Post uploaded.")
                onWritePostSuccess()
            }
            is WritePostUiState.ErrorDuringPostUpload ->
                Text(text = "${(
                        writePostViewModel.writePostUiState as WritePostUiState.ErrorDuringPostUpload).error}")
            else -> {}
        }
    }
}