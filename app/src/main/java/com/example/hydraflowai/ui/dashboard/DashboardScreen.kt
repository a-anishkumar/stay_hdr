package com.example.hydraflowai.ui.dashboard



import androidx.compose.ui.geometry.Rect

import androidx.compose.animation.core.*

import androidx.compose.foundation.Canvas

import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.background

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.*

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.Path

import androidx.compose.ui.graphics.StrokeCap

import androidx.compose.ui.graphics.drawscope.Stroke

import androidx.compose.ui.graphics.drawscope.clipPath

import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

import com.example.hydraflowai.data.local.entity.IntakeRecord

import com.example.hydraflowai.data.model.Beverage

import com.example.hydraflowai.data.weather.ActivityLevel

import java.text.SimpleDateFormat

import java.util.Date

import java.util.Locale

import kotlin.math.sin



@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun DashboardScreen(

    viewModel: DashboardViewModel

) {

    val state by viewModel.uiState.collectAsState()

    val presets by viewModel.presets.collectAsState()



    var showCustomDialog by remember { mutableStateOf(false) }

    var customAmountStr by remember { mutableStateOf("") }

    var selectedBeverage by remember { mutableStateOf(Beverage.WATER) }



    // Preset addition states

    var showAddPresetDialog by remember { mutableStateOf(false) }

    var newPresetName by remember { mutableStateOf("") }

    var newPresetAmount by remember { mutableStateOf("") }



    // Interactive Calculator Bottom Sheet State

    var showCalculator by remember { mutableStateOf(false) }

    var calcWeight by remember { mutableFloatStateOf(state.weightKg) }

    var calcActivity by remember { mutableStateOf(ActivityLevel.ACTIVE) }

    var calcTemp by remember { mutableFloatStateOf(state.weatherTempC) }



    // Wave animation setup

    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    val wavePhase by infiniteTransition.animateFloat(

        initialValue = 0f,

        targetValue = (2 * Math.PI).toFloat(),

        animationSpec = infiniteRepeatable(

            animation = tween(2500, easing = LinearEasing),

            repeatMode = RepeatMode.Restart

        ),

        label = "phase"

    )



    // Helper functions

    fun parseColor(hex: String): Color {

        return try {

            Color(android.graphics.Color.parseColor(hex))

        } catch (e: Exception) {

            Color(0xFF2196F3)

        }

    }



    fun getBeverageIcon(name: String): ImageVector {

        return when {

            name.contains("Coffee", ignoreCase = true) -> Icons.Default.LocalCafe

            name.contains("Tea", ignoreCase = true) -> Icons.Default.LocalCafe

            name.contains("Juice", ignoreCase = true) -> Icons.Default.LocalDrink

            name.contains("Milk", ignoreCase = true) -> Icons.Default.LocalDrink

            name.contains("Soda", ignoreCase = true) -> Icons.Default.LocalDrink

            name.contains("Sports", ignoreCase = true) -> Icons.Default.Bolt

            name.contains("Coconut", ignoreCase = true) -> Icons.Default.Opacity

            else -> Icons.Default.WaterDrop

        }

    }



    LazyColumn(

        modifier = Modifier

            .fillMaxSize()

            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {

        // App Title & Header

        item {

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically

            ) {

                Column {

                    Text(

                        text = "Stay Hydrated",

                        fontSize = 28.sp,

                        fontWeight = FontWeight.Black,

                        color = MaterialTheme.colorScheme.onBackground

                    )

                    Text(

                        text = "Stay Hydrated, Feel Great",

                        fontSize = 14.sp,

                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)

                    )

                }



                // Dynamic calculator action trigger

                IconButton(

                    onClick = {

                        calcWeight = state.weightKg

                        calcTemp = state.weatherTempC

                        showCalculator = true

                    },

                    colors = IconButtonDefaults.iconButtonColors(

                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)

                    )

                ) {

                    Icon(

                        imageVector = Icons.Default.Calculate,

                        contentDescription = "Target Calculator",

                        tint = MaterialTheme.colorScheme.primary

                    )

                }

            }

        }



        // Smart Weather Sync Pill Banner

        item {

            Card(

                shape = RoundedCornerShape(50.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),

                colors = CardDefaults.cardColors(

                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)

                ),

                modifier = Modifier.fillMaxWidth()

            ) {

                Row(

                    modifier = Modifier

                        .padding(horizontal = 16.dp, vertical = 10.dp)

                        .fillMaxWidth(),

                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement = Arrangement.Center

                ) {

                    Icon(

                        imageVector = Icons.Default.WbSunny,

                        contentDescription = "Weather Sync",

                        tint = Color(0xFFFFB300),

                        modifier = Modifier.size(18.dp)

                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(

                        text = "${state.weatherCondition} ${state.weatherTempC.toInt()}Â°C | AI target calibrated",

                        fontSize = 12.sp,

                        fontWeight = FontWeight.Bold,

                        color = MaterialTheme.colorScheme.primary

                    )

                }

            }

        }



        // Circular Progress Ring & Wave Card

        item {

            ElevatedCard(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(28.dp),

                colors = CardDefaults.elevatedCardColors(

                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)

                ),

                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)

            ) {

                Column(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(24.dp),

                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    Box(

                        contentAlignment = Alignment.Center,

                        modifier = Modifier.size(220.dp)

                    ) {

                        val animatedProgress by animateFloatAsState(

                            targetValue = state.completionPercent,

                            animationSpec = tween(durationMillis = 1000),

                            label = "ring"

                        )



                        val primaryColor = MaterialTheme.colorScheme.primary

                        val secondaryColor = MaterialTheme.colorScheme.secondary

                        val trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)



                        // Dual Gradient Progress Ring Brush

                        val ringBrush = Brush.sweepGradient(

                            colors = listOf(

                                primaryColor,

                                secondaryColor,

                                primaryColor

                            )

                        )



                        // 1. Water Wave Fill inside

                        Canvas(

                            modifier = Modifier

                                .size(180.dp)

                                .clip(CircleShape)

                        ) {

                            val w = size.width

                            val h = size.height



                            // Draw track background circle

                            drawCircle(color = trackColor)



                            // Wave logic

                            val progressHeight = (1f - animatedProgress) * h

                            val wavePath = Path().apply {

                                moveTo(0f, h)

                                for (x in 0..w.toInt()) {

                                    // Sine wave formula: baseline + amplitude * sin(x * freq + phase)

                                    val y = progressHeight + (8.dp.toPx() * sin((x / w * 2 * Math.PI) + wavePhase).toFloat())

                                    lineTo(x.toFloat(), y)

                                }

                                lineTo(w, h)

                                close()

                            }



                            // Wave color gradient brush

                            val waveBrush = Brush.verticalGradient(

                                colors = listOf(

                                    primaryColor.copy(alpha = 0.5f),

                                    primaryColor.copy(alpha = 0.9f)

                                )

                            )



                            // Clip wave path to circular layout bounds

                            clipPath(Path().apply { addOval(Rect(0f, 0f, size.width, size.height)) }) {

                                drawPath(path = wavePath, brush = waveBrush)

                            }

                        }



                        // 2. Outer Dual-Gradient Progress Ring

                        Canvas(modifier = Modifier.fillMaxSize()) {

                            // Track circle

                            drawArc(

                                color = trackColor,

                                startAngle = -90f,

                                sweepAngle = 360f,

                                useCenter = false,

                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)

                            )

                            // Progress arc

                            drawArc(

                                brush = ringBrush,

                                startAngle = -90f,

                                sweepAngle = animatedProgress * 360f,

                                useCenter = false,

                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)

                            )

                        }



                        // 3. Central Details Overlay

                        Column(

                            horizontalAlignment = Alignment.CenterHorizontally,

                            modifier = Modifier

                                .clip(CircleShape)

                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))

                                .padding(16.dp)

                        ) {

                            Text(

                                text = "${(state.completionPercent * 100).toInt()}%",

                                fontSize = 38.sp,

                                fontWeight = FontWeight.Black,

                                color = MaterialTheme.colorScheme.onSurface

                            )

                            Text(

                                text = "${state.totalHydratedMl} / ${state.dailyGoalMl} ml",

                                fontSize = 12.sp,

                                fontWeight = FontWeight.Bold,

                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

                            )

                        }

                    }



                    Spacer(modifier = Modifier.height(18.dp))



                    Text(

                        text = if (state.remainingMl > 0) "Remaining: ${state.remainingMl} ml" else "Goal Achieved! You are fully hydrated! ðŸŽ‰",

                        fontSize = 16.sp,

                        fontWeight = FontWeight.Bold,

                        color = if (state.remainingMl > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

                    )

                }

            }

        }



        // Quick add volume controls

        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

            ) {

                Column(

                    modifier = Modifier.padding(16.dp),

                    verticalArrangement = Arrangement.spacedBy(12.dp)

                ) {

                    Text(

                        text = "Quick Log Water",

                        fontSize = 15.sp,

                        fontWeight = FontWeight.Bold,

                        color = MaterialTheme.colorScheme.onSurface

                    )



                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.spacedBy(8.dp)

                    ) {

                        listOf(100, 250, 500, 1000).forEach { amount ->

                            OutlinedCard(

                                onClick = { viewModel.addWater(amount, Beverage.WATER) },

                                modifier = Modifier

                                    .weight(1f)

                                    .height(60.dp),

                                shape = RoundedCornerShape(12.dp),

                                colors = CardDefaults.outlinedCardColors(

                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)

                                ),

                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))

                            ) {

                                Box(

                                    modifier = Modifier.fillMaxSize(),

                                    contentAlignment = Alignment.Center

                                ) {

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                        Icon(

                                            imageVector = Icons.Default.WaterDrop,

                                            contentDescription = null,

                                            tint = MaterialTheme.colorScheme.primary,

                                            modifier = Modifier.size(16.dp)

                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(

                                            text = if (amount >= 1000) "1L" else "${amount}ml",

                                            fontSize = 11.sp,

                                            fontWeight = FontWeight.Black,

                                            color = MaterialTheme.colorScheme.primary

                                        )

                                    }

                                }

                            }

                        }

                    }



                    Button(

                        onClick = { showCustomDialog = true },

                        modifier = Modifier

                            .fillMaxWidth()

                            .height(44.dp),

                        shape = RoundedCornerShape(10.dp),

                        colors = ButtonDefaults.buttonColors(

                            containerColor = MaterialTheme.colorScheme.primary

                        )

                    ) {

                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))

                        Spacer(modifier = Modifier.width(6.dp))

                        Text("Log Special Beverage", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    }

                }

            }

        }



        // Custom Quick Presets Section (New Feature)

        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

            ) {

                Column(

                    modifier = Modifier.padding(16.dp),

                    verticalArrangement = Arrangement.spacedBy(12.dp)

                ) {

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Text(

                            text = "Custom Cup Presets",

                            fontSize = 15.sp,

                            fontWeight = FontWeight.Bold,

                            color = MaterialTheme.colorScheme.onSurface

                        )

                        IconButton(

                            onClick = { showAddPresetDialog = true },

                            modifier = Modifier.size(28.dp),

                            colors = IconButtonDefaults.iconButtonColors(

                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)

                            )

                        ) {

                            Icon(

                                imageVector = Icons.Default.Add,

                                contentDescription = "Add Preset",

                                tint = MaterialTheme.colorScheme.secondary,

                                modifier = Modifier.size(16.dp)

                            )

                        }

                    }



                    if (presets.isEmpty()) {

                        Box(

                            modifier = Modifier

                                .fillMaxWidth()

                                .padding(vertical = 12.dp),

                            contentAlignment = Alignment.Center

                        ) {

                            Text(

                                text = "Create cup presets (e.g., Office Flask, Mug)",

                                fontSize = 12.sp,

                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                            )

                        }

                    } else {

                        // Display user presets in rows

                        Column(

                            verticalArrangement = Arrangement.spacedBy(8.dp),

                            modifier = Modifier.fillMaxWidth()

                        ) {

                            presets.forEach { presetStr ->

                                val parts = presetStr.split(":")

                                val name = parts.getOrNull(0) ?: "Cup"

                                val ml = parts.getOrNull(1)?.toIntOrNull() ?: 250



                                Card(

                                    modifier = Modifier.fillMaxWidth(),

                                    shape = RoundedCornerShape(10.dp),

                                    colors = CardDefaults.cardColors(

                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

                                    )

                                ) {

                                    Row(

                                        modifier = Modifier

                                            .fillMaxWidth()

                                            .padding(horizontal = 12.dp, vertical = 8.dp),

                                        horizontalArrangement = Arrangement.SpaceBetween,

                                        verticalAlignment = Alignment.CenterVertically

                                    ) {

                                        Row(

                                            verticalAlignment = Alignment.CenterVertically,

                                            modifier = Modifier

                                                .weight(1f)

                                                .clickable { viewModel.addWater(ml, Beverage.WATER) }

                                        ) {

                                            Icon(

                                                imageVector = Icons.Default.LocalDrink,

                                                contentDescription = null,

                                                tint = MaterialTheme.colorScheme.secondary,

                                                modifier = Modifier.size(18.dp)

                                            )

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column {

                                                Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                                                Text("Tap to log ${ml}ml", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))

                                            }

                                        }



                                        IconButton(

                                            onClick = { viewModel.deletePreset(presetStr) },

                                            modifier = Modifier.size(24.dp)

                                        ) {

                                            Icon(

                                                imageVector = Icons.Default.Delete,

                                                contentDescription = "Delete Preset",

                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),

                                                modifier = Modifier.size(16.dp)

                                            )

                                        }

                                    }

                                }

                            }

                        }

                    }

                }

            }

        }



        // Low intake dehydration warning banner

        if (state.completionPercent < 0.35f && state.todayLogs.isNotEmpty()) {

            item {

                Card(

                    modifier = Modifier.fillMaxWidth(),

                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),

                    shape = RoundedCornerShape(16.dp)

                ) {

                    Row(

                        modifier = Modifier.padding(16.dp),

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Icon(

                            imageVector = Icons.Default.Info,

                            contentDescription = "Warning",

                            tint = MaterialTheme.colorScheme.error

                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {

                            Text(

                                text = "Hydration level is low",

                                fontWeight = FontWeight.Bold,

                                color = MaterialTheme.colorScheme.onErrorContainer,

                                fontSize = 14.sp

                            )

                            Text(

                                text = "Take a quick sip to maintain optimal energy levels and focus.",

                                fontSize = 12.sp,

                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)

                            )

                        }

                    }

                }

            }

        }



        // Intake logs history header

        item {

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically

            ) {

                Text(

                    text = "Today's Intake History",

                    fontSize = 16.sp,

                    fontWeight = FontWeight.Bold,

                    color = MaterialTheme.colorScheme.onBackground

                )

                Text(

                    text = "${state.todayLogs.size} logs",

                    fontSize = 12.sp,

                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

                )

            }

        }



        // Logs items listing

        if (state.todayLogs.isEmpty()) {

            item {

                Box(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(24.dp),

                    contentAlignment = Alignment.Center

                ) {

                    Text(

                        text = "No beverages logged today yet.",

                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),

                        fontSize = 14.sp

                    )

                }

            }

        } else {

            items(state.todayLogs) { log ->

                val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(log.timestamp))

                

                // Try matching beverage details for custom hex color rendering

                val matchingBev = Beverage.values().find { it.displayName == log.beverageName }

                val bevColor = matchingBev?.let { parseColor(it.colorHex) } ?: MaterialTheme.colorScheme.primary



                Card(

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(14.dp),

                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),

                    colors = CardDefaults.cardColors(

                        containerColor = MaterialTheme.colorScheme.surface

                    )

                ) {

                    Row(

                        modifier = Modifier

                            .fillMaxWidth()

                            .padding(14.dp),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Row(

                            verticalAlignment = Alignment.CenterVertically,

                            modifier = Modifier.weight(1f)

                        ) {

                            // Color-coded round drink avatar with specific drink icons

                            Box(

                                modifier = Modifier

                                    .size(38.dp)

                                    .clip(CircleShape)

                                    .background(bevColor.copy(alpha = 0.15f)),

                                contentAlignment = Alignment.Center

                            ) {

                                Icon(

                                    imageVector = getBeverageIcon(log.beverageName),

                                    contentDescription = null,

                                    tint = bevColor,

                                    modifier = Modifier.size(18.dp)

                                )

                            }



                            Spacer(modifier = Modifier.width(12.dp))



                            Column {

                                Text(

                                    text = log.beverageName,

                                    fontWeight = FontWeight.Bold,

                                    fontSize = 14.sp,

                                    color = MaterialTheme.colorScheme.onSurface

                                )

                                Text(

                                    text = "$timeStr | Efficiency: ${(log.hydrationScore * 100).toInt()}%",

                                    fontSize = 12.sp,

                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                                )

                            }

                        }



                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(

                                text = "${log.amountMl} ml",

                                fontWeight = FontWeight.Black,

                                color = bevColor,

                                modifier = Modifier.padding(end = 6.dp),

                                fontSize = 14.sp

                            )

                            IconButton(

                                onClick = { viewModel.deleteIntake(log) },

                                modifier = Modifier.size(32.dp)

                            ) {

                                Icon(

                                    imageVector = Icons.Default.Delete,

                                    contentDescription = "Delete entry",

                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),

                                    modifier = Modifier.size(16.dp)

                                )

                            }

                        }

                    }

                }

            }

        }



        item {

            Spacer(modifier = Modifier.height(64.dp))

        }

    }



    // Special Beverage Custom Selection Dialog

    if (showCustomDialog) {

        AlertDialog(

            onDismissRequest = { showCustomDialog = false },

            title = { Text("Log Drink", fontWeight = FontWeight.Bold) },

            text = {

                Column(

                    verticalArrangement = Arrangement.spacedBy(10.dp)

                ) {

                    Text("Select Beverage Type:", fontSize = 13.sp)



                    // Grid flow list

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement = Arrangement.spacedBy(6.dp)

                        ) {

                            Beverage.values().take(4).forEach { bev ->

                                val selected = selectedBeverage == bev

                                val col = parseColor(bev.colorHex)

                                Card(

                                    modifier = Modifier

                                        .weight(1f)

                                        .clickable { selectedBeverage = bev },

                                    colors = CardDefaults.cardColors(

                                        containerColor = if (selected) col.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

                                    ),

                                    border = if (selected) BorderStroke(1.dp, col) else null

                                ) {

                                    Box(

                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),

                                        contentAlignment = Alignment.Center

                                    ) {

                                        Text(

                                            bev.displayName,

                                            fontSize = 9.sp,

                                            fontWeight = FontWeight.Bold,

                                            color = if (selected) col else MaterialTheme.colorScheme.onSurfaceVariant

                                        )

                                    }

                                }

                            }

                        }

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement = Arrangement.spacedBy(6.dp)

                        ) {

                            Beverage.values().takeLast(4).forEach { bev ->

                                val selected = selectedBeverage == bev

                                val col = parseColor(bev.colorHex)

                                Card(

                                    modifier = Modifier

                                        .weight(1f)

                                        .clickable { selectedBeverage = bev },

                                    colors = CardDefaults.cardColors(

                                        containerColor = if (selected) col.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

                                    ),

                                    border = if (selected) BorderStroke(1.dp, col) else null

                                ) {

                                    Box(

                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),

                                        contentAlignment = Alignment.Center

                                    ) {

                                        Text(

                                            bev.displayName,

                                            fontSize = 9.sp,

                                            fontWeight = FontWeight.Bold,

                                            color = if (selected) col else MaterialTheme.colorScheme.onSurfaceVariant

                                        )

                                    }

                                }

                            }

                        }

                    }



                    Spacer(modifier = Modifier.height(6.dp))



                    TextField(

                        value = customAmountStr,

                        onValueChange = { customAmountStr = it },

                        label = { Text("Volume in ml") },

                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

                        modifier = Modifier.fillMaxWidth()

                    )

                }

            },

            confirmButton = {

                Button(

                    onClick = {

                        val amount = customAmountStr.toIntOrNull() ?: 250

                        viewModel.addWater(amount, selectedBeverage)

                        showCustomDialog = false

                        customAmountStr = ""

                    }

                ) {

                    Text("Add")

                }

            },

            dismissButton = {

                TextButton(onClick = { showCustomDialog = false }) {

                    Text("Cancel")

                }

            }

        )

    }



    // Add Custom Preset Dialog

    if (showAddPresetDialog) {

        AlertDialog(

            onDismissRequest = { showAddPresetDialog = false },

            title = { Text("New Cup Preset", fontWeight = FontWeight.Bold) },

            text = {

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    TextField(

                        value = newPresetName,

                        onValueChange = { newPresetName = it },

                        label = { Text("Preset Name (e.g., Office Flask)") },

                        modifier = Modifier.fillMaxWidth()

                    )

                    TextField(

                        value = newPresetAmount,

                        onValueChange = { newPresetAmount = it },

                        label = { Text("Capacity in ml") },

                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

                        modifier = Modifier.fillMaxWidth()

                    )

                }

            },

            confirmButton = {

                Button(

                    onClick = {

                        val amt = newPresetAmount.toIntOrNull()

                        if (newPresetName.isNotBlank() && amt != null) {

                            viewModel.addPreset(newPresetName, amt)

                            showAddPresetDialog = false

                            newPresetName = ""

                            newPresetAmount = ""

                        }

                    }

                ) {

                    Text("Save Preset")

                }

            },

            dismissButton = {

                TextButton(onClick = { showAddPresetDialog = false }) {

                    Text("Cancel")

                }

            }

        )

    }



    // Goal Target Calculator Dialog (Interactive Quiz/Calculation Sheet)

    if (showCalculator) {

        AlertDialog(

            onDismissRequest = { showCalculator = false },

            title = { Text("Smart Target Calculator", fontWeight = FontWeight.Bold) },

            text = {

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    Text(

                        text = "Customize baseline factors to recalculate your target hydration goal.",

                        fontSize = 12.sp,

                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

                    )



                    // Weight setting

                    Column {

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement = Arrangement.SpaceBetween

                        ) {

                            Text("Your Weight", fontSize = 12.sp)

                            Text("${calcWeight.toInt()} kg", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        }

                        Slider(

                            value = calcWeight,

                            onValueChange = { calcWeight = it },

                            valueRange = 30f..150f

                        )

                    }



                    // Activity selection

                    Column {

                        Text("Daily Activity Intensity", fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement = Arrangement.spacedBy(4.dp)

                        ) {

                            ActivityLevel.values().forEach { level ->

                                val active = calcActivity == level

                                OutlinedButton(

                                    onClick = { calcActivity = level },

                                    modifier = Modifier.weight(1f),

                                    shape = RoundedCornerShape(6.dp),

                                    colors = ButtonDefaults.outlinedButtonColors(

                                        containerColor = if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent

                                    )

                                ) {

                                    Text(

                                        level.displayName.split(' ').first(),

                                        fontSize = 10.sp,

                                        fontWeight = FontWeight.Bold,

                                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

                                    )

                                }

                            }

                        }

                    }



                    // Temperature simulation slider

                    Column {

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement = Arrangement.SpaceBetween

                        ) {

                            Text("Current Climate Temp", fontSize = 12.sp)

                            Text("${calcTemp.toInt()}Â°C", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        }

                        Slider(

                            value = calcTemp,

                            onValueChange = { calcTemp = it },

                            valueRange = 0f..45f

                        )

                    }



                    Spacer(modifier = Modifier.height(6.dp))



                    // Computed preview

                    val computedTarget = remember(calcWeight, calcActivity, calcTemp) {

                        val base = calcWeight * 35f

                        val tempAdjust = if (calcTemp > 22f) ((calcTemp - 22f) * 30f).toInt().coerceAtMost(1000) else 0

                        (base.toInt() + calcActivity.adjustmentMl + tempAdjust)

                    }



                    Card(

                        modifier = Modifier.fillMaxWidth(),

                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))

                    ) {

                        Row(

                            modifier = Modifier

                                .fillMaxWidth()

                                .padding(12.dp),

                            horizontalArrangement = Arrangement.SpaceBetween,

                            verticalAlignment = Alignment.CenterVertically

                        ) {

                            Text("Recommended Goal:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                            Text("${computedTarget} ml", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)

                        }

                    }

                }

            },

            confirmButton = {

                Button(

                    onClick = {

                        val base = calcWeight * 35f

                        val tempAdjust = if (calcTemp > 22f) ((calcTemp - 22f) * 30f).toInt().coerceAtMost(1000) else 0

                        val computed = base.toInt() + calcActivity.adjustmentMl + tempAdjust

                        

                        viewModel.updateGoal(computed)

                        viewModel.updateWeight(calcWeight)

                        viewModel.updateActivityLevel(calcActivity)

                        showCalculator = false

                    }

                ) {

                    Text("Apply Target")

                }

            },

            dismissButton = {

                TextButton(onClick = { showCalculator = false }) {

                    Text("Cancel")

                }

            }

        )

    }

}



