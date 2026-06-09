package com.example.hydraflowai.ui.settings



import android.widget.Toast

import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.*

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

import com.example.hydraflowai.data.repository.SyncState

import com.example.hydraflowai.data.repository.WaterRepository

import com.example.hydraflowai.ui.dashboard.DashboardViewModel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.background

import androidx.compose.foundation.clickable

import kotlinx.coroutines.launch

import kotlin.math.roundToInt



@Composable

fun SettingsScreen(

    repository: WaterRepository,

    dashboardViewModel: DashboardViewModel

) {

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    

    var weight by remember { mutableFloatStateOf(repository.getUserWeight()) }

    var cloudSyncStatus by remember { mutableStateOf(SyncState.IDLE) }

    var watchSyncStatus by remember { mutableStateOf(SyncState.IDLE) }

    var fitSyncEnabled by remember { mutableStateOf(true) }



    // Smart notifications config state from ViewModel

    val remindersEnabled by dashboardViewModel.remindersEnabled.collectAsState()

    val reminderInterval by dashboardViewModel.reminderInterval.collectAsState()



    val dashboardState by dashboardViewModel.uiState.collectAsState()

    val googleAccounts = dashboardViewModel.googleAccounts
    val isLoggedIn by dashboardViewModel.isLoggedIn.collectAsState()

    val userName by dashboardViewModel.userName.collectAsState()

    val userEmail by dashboardViewModel.userEmail.collectAsState()

    var showAccountSelector by remember { mutableStateOf(false) }



    LazyColumn(

        modifier = Modifier

            .fillMaxSize()

            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        // Header

        item {

            Column(modifier = Modifier.fillMaxWidth()) {

                Text(

                    text = "Profile & Settings",

                    fontSize = 28.sp,

                    fontWeight = FontWeight.Black,

                    color = MaterialTheme.colorScheme.onBackground

                )

                Text(

                    text = "Manage configurations and cloud syncs",

                    fontSize = 14.sp,

                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)

                )

            }

        }



        // Google Profile Section

        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(24.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

            ) {

                Column(

                    modifier = Modifier.padding(20.dp),

                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    if (isLoggedIn) {

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            verticalAlignment = Alignment.CenterVertically

                        ) {

                            Box(

                                modifier = Modifier

                                    .size(52.dp)

                                    .clip(CircleShape)

                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),

                                contentAlignment = Alignment.Center

                            ) {

                                Text(

                                    text = userName.take(1).uppercase(),

                                    fontWeight = FontWeight.Black,

                                    fontSize = 20.sp,

                                    color = MaterialTheme.colorScheme.primary

                                )

                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {

                                Text(

                                    text = userName,

                                    fontWeight = FontWeight.Black,

                                    fontSize = 16.sp,

                                    color = MaterialTheme.colorScheme.onSurface

                                )

                                Text(

                                    text = userEmail,

                                    fontSize = 12.sp,

                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                                )

                            }

                        }

                        

                        Spacer(modifier = Modifier.height(16.dp))

                        

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement = Arrangement.spacedBy(10.dp)

                        ) {

                            OutlinedButton(

                                onClick = { showAccountSelector = true },

                                modifier = Modifier.weight(1f),

                                shape = RoundedCornerShape(10.dp)

                            ) {

                                Icon(Icons.Default.SwitchAccount, contentDescription = null, modifier = Modifier.size(16.dp))

                                Spacer(modifier = Modifier.width(6.dp))

                                Text("Switch", fontSize = 12.sp)

                            }

                            Button(

                                onClick = { dashboardViewModel.signOut() },

                                modifier = Modifier.weight(1f),

                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)),

                                shape = RoundedCornerShape(10.dp)

                            ) {

                                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))

                                Spacer(modifier = Modifier.width(6.dp))

                                Text("Sign Out", fontSize = 12.sp, color = Color.White)

                            }

                        }

                    } else {

                        Column(

                            horizontalAlignment = Alignment.CenterHorizontally,

                            modifier = Modifier.fillMaxWidth()

                        ) {

                            Box(

                                modifier = Modifier

                                    .size(48.dp)

                                    .clip(CircleShape)

                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),

                                contentAlignment = Alignment.Center

                            ) {

                                Icon(

                                    imageVector = Icons.Default.Person,

                                    contentDescription = null,

                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

                                )

                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(

                                text = "Logged in as Guest",

                                fontWeight = FontWeight.Bold,

                                fontSize = 14.sp

                            )

                            Text(

                                text = "Sign in to back up data to cloud database",

                                fontSize = 12.sp,

                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(

                                onClick = { showAccountSelector = true },

                                modifier = Modifier.fillMaxWidth(),

                                shape = RoundedCornerShape(10.dp)

                            ) {

                                Text("Sign in with Google", fontWeight = FontWeight.Bold)

                            }

                        }

                    }

                }

            }

        }



        // Weight Configuration Card

        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

            ) {

                Column(

                    modifier = Modifier.padding(20.dp),

                    horizontalAlignment = Alignment.Start

                ) {

                    Row(

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Icon(

                            imageVector = Icons.Default.MonitorWeight, 

                            contentDescription = "Weight", 

                            tint = MaterialTheme.colorScheme.primary,

                            modifier = Modifier.size(20.dp)

                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text("Personal Profile Weight", fontWeight = FontWeight.Bold)

                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(

                        text = "We adjust your baseline hydration needs as your weight changes:", 

                        fontSize = 12.sp, 

                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Text("Current Weight:", fontSize = 14.sp)

                        Text(

                            text = "${weight.toInt()} kg", 

                            fontWeight = FontWeight.Black, 

                            color = MaterialTheme.colorScheme.primary, 

                            fontSize = 22.sp

                        )

                    }

                    Slider(

                        value = weight,

                        onValueChange = {

                            weight = it

                            dashboardViewModel.updateWeight(it)

                        },

                        valueRange = 30f..150f

                    )

                }

            }

        }



        // Custom Water Goal Card

        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

            ) {

                Column(

                    modifier = Modifier.padding(20.dp),

                    horizontalAlignment = Alignment.Start

                ) {

                    Row(

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Icon(

                            imageVector = Icons.Default.WaterDrop,

                            contentDescription = "Water Goal",

                            tint = MaterialTheme.colorScheme.primary,

                            modifier = Modifier.size(20.dp)

                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text("Daily Hydration Target", fontWeight = FontWeight.Bold)

                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(

                        text = "Manually adjust your daily target goal in milliliters (ml):",

                        fontSize = 12.sp,

                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    

                    val currentGoal = dashboardState.dailyGoalMl

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Text("Daily Goal Target:", fontSize = 14.sp)

                        Text(

                            text = "${currentGoal} ml",

                            fontWeight = FontWeight.Black,

                            color = MaterialTheme.colorScheme.primary,

                            fontSize = 22.sp

                        )

                    }

                    Slider(

                        value = currentGoal.toFloat(),

                        onValueChange = {

                            dashboardViewModel.updateGoal(it.roundToInt())

                        },

                        valueRange = 1000f..5000f,

                        steps = 40

                    )

                    

                    Spacer(modifier = Modifier.height(8.dp))

                    

                    OutlinedButton(

                        onClick = {

                            scope.launch {

                                val rec = repository.getOrCreateDailyGoal()

                                dashboardViewModel.updateGoal(rec)

                            }

                        },

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(10.dp),

                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))

                    ) {

                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))

                        Spacer(modifier = Modifier.width(6.dp))

                        Text("Reset to AI Recommended Goal", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    }

                }

            }

        }



        // NEW FEATURE: Smart Hydration Reminders Card

        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

            ) {

                Column(

                    modifier = Modifier.padding(20.dp),

                    horizontalAlignment = Alignment.Start,

                    verticalArrangement = Arrangement.spacedBy(14.dp)

                ) {

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Icon(

                                imageVector = Icons.Default.Notifications,

                                contentDescription = "Reminders",

                                tint = MaterialTheme.colorScheme.primary,

                                modifier = Modifier.size(20.dp)

                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text("Smart Drink Reminders", fontWeight = FontWeight.Bold)

                        }



                        Switch(

                            checked = remindersEnabled,

                            onCheckedChange = { dashboardViewModel.setRemindersEnabled(it) }

                        )

                    }



                    Text(

                        text = "Receive intelligent, periodic reminders to drink water based on your calculated pace.",

                        fontSize = 12.sp,

                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                    )



                    if (remindersEnabled) {

                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                            Row(

                                modifier = Modifier.fillMaxWidth(),

                                horizontalArrangement = Arrangement.SpaceBetween,

                                verticalAlignment = Alignment.CenterVertically

                            ) {

                                Text("Reminder Interval:", fontSize = 13.sp)

                                val formattedInterval = (reminderInterval * 10).roundToInt() / 10f

                                Text(

                                    text = "Every $formattedInterval hours", 

                                    fontWeight = FontWeight.Black, 

                                    color = MaterialTheme.colorScheme.primary,

                                    fontSize = 15.sp

                                )

                            }

                            

                            Slider(

                                value = reminderInterval,

                                onValueChange = { dashboardViewModel.setReminderInterval(it) },

                                valueRange = 0.5f..5.0f

                            )

                        }

                    }

                }

            }

        }



        // Google Cloud Sync options

        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

            ) {

                Column(

                    modifier = Modifier.padding(20.dp),

                    horizontalAlignment = Alignment.Start,

                    verticalArrangement = Arrangement.spacedBy(16.dp)

                ) {

                    Row(

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Icon(

                            imageVector = Icons.Default.CloudSync, 

                            contentDescription = "Cloud", 

                            tint = MaterialTheme.colorScheme.tertiary,

                            modifier = Modifier.size(20.dp)

                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text("Google Cloud Database Sync", fontWeight = FontWeight.Bold)

                    }



                    Text(

                        text = "Synchronize data and settings automatically to Google Firestore.",

                        fontSize = 12.sp,

                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                    )



                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Text(

                            text = "Sync Status: ${cloudSyncStatus.name}",

                            fontSize = 13.sp,

                            fontWeight = FontWeight.Bold,

                            color = when (cloudSyncStatus) {

                                SyncState.SUCCESS -> MaterialTheme.colorScheme.secondary

                                SyncState.SYNCING -> MaterialTheme.colorScheme.primary

                                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

                            }

                        )



                        Button(

                            onClick = {

                                scope.launch {

                                    repository.syncWithCloud().collect { status ->

                                        cloudSyncStatus = status

                                    }

                                }

                            },

                            enabled = cloudSyncStatus != SyncState.SYNCING,

                            shape = RoundedCornerShape(8.dp)

                        ) {

                            Text("Sync Now", fontWeight = FontWeight.Bold)

                        }

                    }

                }

            }

        }



        // Smartwatch Sync

        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

            ) {

                Column(

                    modifier = Modifier.padding(20.dp),

                    horizontalAlignment = Alignment.Start,

                    verticalArrangement = Arrangement.spacedBy(16.dp)

                ) {

                    Row(

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Icon(

                            imageVector = Icons.Default.Watch, 

                            contentDescription = "Watch", 

                            tint = MaterialTheme.colorScheme.secondary,

                            modifier = Modifier.size(20.dp)

                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text("Wear OS Smartwatch Integration", fontWeight = FontWeight.Bold)

                    }



                    Text(

                        text = "Synchronize logs and current goals with your paired Wear OS watch.",

                        fontSize = 12.sp,

                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                    )



                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Text(

                            text = "Watch Status: ${watchSyncStatus.name}",

                            fontSize = 13.sp,

                            fontWeight = FontWeight.Bold,

                            color = when (watchSyncStatus) {

                                SyncState.SUCCESS -> MaterialTheme.colorScheme.secondary

                                SyncState.SYNCING -> MaterialTheme.colorScheme.primary

                                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

                            }

                        )



                        Button(

                            onClick = {

                                scope.launch {

                                    repository.syncWithHealthConnect().collect { status ->

                                        watchSyncStatus = status

                                    }

                                }

                            },

                            enabled = watchSyncStatus != SyncState.SYNCING,

                            shape = RoundedCornerShape(8.dp)

                        ) {

                            Text("Sync Watch", fontWeight = FontWeight.Bold)

                        }

                    }

                }

            }

        }



        // Google Fit Integration

        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

            ) {

                Row(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(20.dp),

                    horizontalArrangement = Arrangement.SpaceBetween,

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(

                            imageVector = Icons.Default.Favorite, 

                            contentDescription = "Fit", 

                            tint = MaterialTheme.colorScheme.error,

                            modifier = Modifier.size(20.dp)

                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {

                            Text("Health Connect / Fit", fontWeight = FontWeight.Bold)

                            Text("Share water logs automatically", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))

                        }

                    }



                    Switch(

                        checked = fitSyncEnabled,

                        onCheckedChange = {

                            fitSyncEnabled = it

                            Toast.makeText(context, if (it) "Health Connect Enabled" else "Health Connect Disabled", Toast.LENGTH_SHORT).show()

                        }

                    )

                }

            }

        }



        item {

            Spacer(modifier = Modifier.height(64.dp))

        }

    }



    // Google Account Selector Dialog

    if (showAccountSelector) {

        AlertDialog(

            onDismissRequest = { showAccountSelector = false },

            title = { Text("Select Account", fontWeight = FontWeight.Bold, fontSize = 20.sp) },

            text = {

                Column(

                    verticalArrangement = Arrangement.spacedBy(8.dp),

                    modifier = Modifier.fillMaxWidth()

                ) {

                    googleAccounts.forEach { email ->

                        val name = when (email) {

                            "anishkumar.a2006@gmail.com" -> "Anish Kumar"

                            "anish.hydraflow@gmail.com" -> "Anish Hydra"

                            else -> "Hydra Guest"

                        }

                        

                        Card(

                            modifier = Modifier

                                .fillMaxWidth()

                                .clickable {

                                    dashboardViewModel.signIn(email)

                                    showAccountSelector = false

                                    Toast.makeText(context, "Logged in as $name", Toast.LENGTH_SHORT).show()

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

                TextButton(onClick = { showAccountSelector = false }) {

                    Text("Cancel")

                }

            }

        )

    }

}

