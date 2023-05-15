package com.example.newsroom.ui.screen.reporters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.newsroom.data.Reporter
import com.example.newsroom.ui.screen.reporters.ReporterScreenUIState
import com.example.newsroom.ui.screen.reporters.ReporterScreenViewModel
import com.example.newsroom.ui.screen.signup.RegisterUIState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporterScreen(
    reporterScreenViewModel: ReporterScreenViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val reporterListState = reporterScreenViewModel.reportersList().collectAsState(initial = ReporterScreenUIState.Init)

    Scaffold(
        topBar = { MainTopBar(title = "Reporters") },
    ) { contentPadding ->
        // Screen content
        Column(modifier = Modifier.padding(contentPadding)) {
            if (reporterListState.value == ReporterScreenUIState.Init) {
                Text(text = "Initializing..")
            } else if (reporterListState.value is ReporterScreenUIState.Success) {
                LazyColumn() {
                    items((reporterListState.value as ReporterScreenUIState.Success).reporterList){
                        ReporterCard(reporter = it,
                            currentUserId = reporterScreenViewModel.currentUserId,
                            reporterScreenViewModel = reporterScreenViewModel)
                    }
                }
            }
            when (reporterScreenViewModel.reporterScreenUIState){
                is ReporterScreenUIState.Loading -> CircularProgressIndicator()
                is ReporterScreenUIState.Error -> Text(text = "Error: ${
                    (reporterScreenViewModel.reporterScreenUIState as ReporterScreenUIState.Error).error
                }")
                is ReporterScreenUIState.FollowerExists -> {
                    Text(text = "Already following")
                }
                is ReporterScreenUIState.FollowerCollectionAdded -> {
                    Text(text = "Follower added")
                }
                ReporterScreenUIState.Init -> {}
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(title: String) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =
            MaterialTheme.colorScheme.secondaryContainer
        ),
        actions = {
            IconButton(
                onClick = { }
            ) {
                Icon(Icons.Filled.Info, contentDescription = "Info")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporterCard(
    reporter: Reporter,
    currentUserId: String = "",
    reporterScreenViewModel: ReporterScreenViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
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
                        text = reporter.author,
                        fontSize = 18.sp
                    )
                    Text(
                        text = reporter.email,
                        fontSize = 12.sp
                    )
                }

                Button(onClick = {
                    coroutineScope.launch {
                        reporterScreenViewModel.createFollower(reporter.uid, reporter.author, reporter.email)
                    }
                }) {
                    Text(text = "Follow")
                }
            }
        }
    }
}