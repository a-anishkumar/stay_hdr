package com.example.hydraflowai.ui.auth



import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

import kotlinx.coroutines.delay

import kotlinx.coroutines.launch

import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.background

import androidx.compose.foundation.BorderStroke



@Composable

fun LoginScreen(

    onLoginSuccess: (String) -> Unit

) {

    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

    var showAccountDialog by remember { mutableStateOf(false) }

    

    val accounts = listOf(

        "anishkumar.a2006@gmail.com",

        "anish.hydraflow@gmail.com",

        "guest.hydraflow@gmail.com"

    )



    Box(

        modifier = Modifier

            .fillMaxSize()

            .background(

                brush = Brush.verticalGradient(

                    colors = listOf(

                        MaterialTheme.colorScheme.background,

                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)

                    )

                )

            )

            .padding(24.dp),

        contentAlignment = Alignment.Center

    ) {

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(24.dp),

            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),

            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)

        ) {

            Column(

                modifier = Modifier.padding(24.dp),

                horizontalAlignment = Alignment.CenterHorizontally,

                verticalArrangement = Arrangement.Center

            ) {

                Text(

                    text = "Welcome to HydraFlow AI",

                    fontSize = 24.sp,

                    fontWeight = FontWeight.Bold,

                    color = MaterialTheme.colorScheme.onSurface

                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(

                    text = "Sync your daily water logs to Cloud and Google Fit automatically.",

                    fontSize = 14.sp,

                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),

                    modifier = Modifier.fillMaxWidth()

                )

                Spacer(modifier = Modifier.height(48.dp))



                if (isLoading) {

                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(

                        text = "Authenticating with Google...",

                        fontSize = 14.sp,

                        color = MaterialTheme.colorScheme.primary

                    )

                } else {

                    Button(

                        onClick = { showAccountDialog = true },

                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),

                        modifier = Modifier

                            .fillMaxWidth()

                            .height(54.dp)

                    ) {

                        Text(

                            text = "Sign in with Google",

                            fontSize = 16.sp,

                            fontWeight = FontWeight.Bold,

                            color = Color.White

                        )

                    }



                    Spacer(modifier = Modifier.height(12.dp))



                    TextButton(

                        onClick = { onLoginSuccess("guest.hydraflow@gmail.com") },

                        modifier = Modifier

                            .fillMaxWidth()

                            .height(48.dp)

                    ) {

                        Text(

                            text = "Skip & Continue as Guest",

                            fontSize = 14.sp,

                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                        )

                    }

                }

            }

        }

    }



    // Google Account Selector Dialog

    if (showAccountDialog) {

        AlertDialog(

            onDismissRequest = { showAccountDialog = false },

            title = { Text("Choose an account", fontWeight = FontWeight.Bold, fontSize = 20.sp) },

            text = {

                Column(

                    verticalArrangement = Arrangement.spacedBy(8.dp),

                    modifier = Modifier.fillMaxWidth()

                ) {

                    Text(

                        text = "to continue to HydraFlow AI",

                        fontSize = 13.sp,

                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    

                    accounts.forEach { email ->

                        val name = when (email) {

                            "anishkumar.a2006@gmail.com" -> "Anish Kumar"

                            "anish.hydraflow@gmail.com" -> "Anish Hydra"

                            else -> "Hydra Guest"

                        }

                        

                        Card(

                            modifier = Modifier

                                .fillMaxWidth()

                                .clickable {

                                    showAccountDialog = false

                                    scope.launch {

                                        isLoading = true

                                        delay(1000)

                                        isLoading = false

                                        onLoginSuccess(email)

                                    }

                                },

                            shape = RoundedCornerShape(12.dp),

                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),

                            colors = CardDefaults.cardColors(

                                containerColor = MaterialTheme.colorScheme.surface

                            )

                        ) {

                            Row(

                                modifier = Modifier

                                    .fillMaxWidth()

                                    .padding(12.dp),

                                verticalAlignment = Alignment.CenterVertically

                            ) {

                                Box(

                                    modifier = Modifier

                                        .size(36.dp)

                                        .clip(CircleShape)

                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),

                                    contentAlignment = Alignment.Center

                                ) {

                                    Text(

                                        text = name.take(1).uppercase(),

                                        fontWeight = FontWeight.Black,

                                        color = MaterialTheme.colorScheme.primary,

                                        fontSize = 14.sp

                                    )

                                }

                                

                                Spacer(modifier = Modifier.width(12.dp))

                                

                                Column {

                                    Text(

                                        text = name,

                                        fontWeight = FontWeight.Bold,

                                        fontSize = 13.sp,

                                        color = MaterialTheme.colorScheme.onSurface

                                    )

                                    Text(

                                        text = email,

                                        fontSize = 11.sp,

                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                                    )

                                }

                            }

                        }

                    }

                }

            },

            confirmButton = {},

            dismissButton = {

                TextButton(onClick = { showAccountDialog = false }) {

                    Text("Cancel")

                }

            }

        )

    }

}

