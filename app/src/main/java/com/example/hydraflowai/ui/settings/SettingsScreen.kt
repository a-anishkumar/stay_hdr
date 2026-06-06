package com.example.hydraflowai.ui.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Watch
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Manage configurations and cloud syncs",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        // Weight Configuration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MonitorWeight, contentDescription = "Weight", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Personal Profile Weight", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("We adjust your baseline hydration needs as your weight changes:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Current Weight:", fontSize = 14.sp)
                        Text("${weight.toInt()} kg", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
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

        // Google sync options
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
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
                        Icon(Icons.Default.CloudSync, contentDescription = "Cloud", tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Google Cloud Database Sync", fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "Synchronize data and settings automatically to Google Firestore.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sync Status: ${cloudSyncStatus.name}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
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
                            Text("Sync Now")
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
                        Icon(Icons.Default.Watch, contentDescription = "Watch", tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Wear OS Smartwatch Integration", fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "Synchronize logs and current goals with your paired Wear OS watch.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Watch Status: ${watchSyncStatus.name}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
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
                            Text("Sync Watch")
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
                        Icon(Icons.Default.Favorite, contentDescription = "Fit", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Health Connect / Fit", fontWeight = FontWeight.Bold)
                            Text("Share water logs automatically", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
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
}
