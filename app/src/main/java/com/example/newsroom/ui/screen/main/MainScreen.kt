package com.example.newsroom.ui.screen.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.newsroom.data.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onWriteNewPostClick: () -> Unit = {},
    showReportersClick: () -> Unit = {},
    mainScreenViewModel: MainScreenViewModel = viewModel(),
    showOneReporterClick: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val postListState = mainScreenViewModel.postsList().collectAsState(initial = MainScreenUIState.Init)

    Scaffold(
        topBar = { MainTopBar(
            title = "NewsRoom",
            onReportersClick = showReportersClick) },
        floatingActionButton = {
            if(mainScreenViewModel.currentUser?.reporter?: false) {
                MainFloatingActionButton(
                    onWriteNewPostClick = onWriteNewPostClick,
                )
            }

        }

    ) { contentPadding ->
        // Screen content
        Column(modifier = Modifier.padding(contentPadding)) {
            if (postListState.value == MainScreenUIState.Init) {
                Text(text = "Initializing..")
            }
            else if (postListState.value is MainScreenUIState.Success) {
                when(mainScreenViewModel.userUIState){
                    is GetUserUIState.Error -> Text(text = "Error Connecting to Server")
                    is GetUserUIState.Success -> Text("User: ${mainScreenViewModel.currentUser!!.name}")
                    is GetUserUIState.Loading -> Text("Connecting to Server...")
                    is GetUserUIState.Init -> Text("Initializing...")
                }

                LazyColumn() {
                    items((postListState.value as MainScreenUIState.Success).postList){
                        PostCard(post = it.post,
                            onRemoveItem = {
                                mainScreenViewModel.deletePost(it.postId)
                            },
                            currentUserId = mainScreenViewModel.currentUserId,
                            showReporterScreen = showOneReporterClick,
                            mainScreenViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainFloatingActionButton(
    onWriteNewPostClick: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    FloatingActionButton(
        onClick = {
            onWriteNewPostClick()
        },
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = RoundedCornerShape(16.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add",
            tint = Color.White,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(title: String, onReportersClick: () -> Unit = {}) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =
            MaterialTheme.colorScheme.secondaryContainer
        ),
        actions = {
            IconButton(
                onClick = {
                    onReportersClick()
                }
            ) {
                Icon(Icons.Filled.Info, contentDescription = "Info")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCard(
    post: Post,
    onRemoveItem: () -> Unit = {},
    currentUserId: String = "",
    showReporterScreen: () -> Unit,
    mainScreenViewModel: MainScreenViewModel
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        modifier = Modifier.padding(5.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = post.author,
                        Modifier.clickable { showReporterScreen() }
                    )
                    Text(
                        text = post.body,
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = "Save",
                    modifier = Modifier.clickable {
                        mainScreenViewModel.savePost(post)
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentUserId.equals(post.authoruid)) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.clickable {
                                onRemoveItem()
                            },
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}