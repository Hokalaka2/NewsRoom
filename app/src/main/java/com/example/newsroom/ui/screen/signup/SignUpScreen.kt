package com.example.newsroom.ui.screen.signup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.newsroom.ui.screen.login.LoginUiState
import com.example.newsroom.ui.screen.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SignUpScreen(
    registerViewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("nytimes@gmail.com") }
    var name by rememberSaveable { mutableStateOf("New York Times") }
    var reporterCheckBox by rememberSaveable { mutableStateOf(false) }
    var password by rememberSaveable { mutableStateOf("password") }
    val coroutineScope = rememberCoroutineScope()

    Box() {
        Text(
            text = "AIT Forum",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            fontSize = 30.sp
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(text = "Name")
                },
                value = name,
                onValueChange = {
                   name = it
                },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(text = "E-mail")
                },
                value = email,
                onValueChange = {
                    email = it
                },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Email, null)
                }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(text = "Password")
                },
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(Icons.Default.Password, null)
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        if (showPassword) {
                            Icon(Icons.Default.Visibility, null)
                        } else {
                            Icon(Icons.Default.VisibilityOff, null)
                        }
                    }
                }
            )
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text="Reporter: ",
                    fontSize = 18.sp
                )
                Checkbox(checked = reporterCheckBox, onCheckedChange = {
                    reporterCheckBox = it
                })
            }
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = {
                    // do registration..
                    coroutineScope.launch {
                        registerViewModel.registerUser(email, password)

                         /**  var result = loginViewModel.loginUser(email, password)
                            if(result?.user != null) {
                                withContext(Dispatchers.Main) {
                                    onRegisterSuccess()
                                }
                            }
                            */

                    }
                }) {
                    Text(text = "Register")
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (registerViewModel.registerUIState){
                is RegisterUIState.Loading -> CircularProgressIndicator()
                is RegisterUIState.Error -> Text(text = "Error: ${
                    (registerViewModel.registerUIState as RegisterUIState.Error).error
                }")
                is RegisterUIState.RegisterSuccess -> {
                    Text(text = "Registration OK")

                    if(reporterCheckBox){
                        registerViewModel.createReporter()
                    }
                }
                is RegisterUIState.ReporterCollectionAdded -> {
                    Text(text = "Reporter added")
                }
                RegisterUIState.Init -> {}
            }
        }
    }
}