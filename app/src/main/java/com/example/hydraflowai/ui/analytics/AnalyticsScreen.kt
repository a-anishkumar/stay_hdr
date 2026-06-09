package com.example.hydraflowai.ui.analytics



import androidx.compose.foundation.border

import android.content.Context

import android.graphics.Paint

import android.graphics.pdf.PdfDocument

import android.os.Environment

import android.widget.Toast

import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.*

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

import com.example.hydraflowai.data.model.Beverage

import java.io.File

import java.io.FileOutputStream

import java.text.SimpleDateFormat

import java.util.*



@Composable

fun AnalyticsScreen(

    viewModel: AnalyticsViewModel

) {

    val state by viewModel.uiState.collectAsState()

    val context = LocalContext.current



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

        // Screen Header

        item {

            Column(modifier = Modifier.fillMaxWidth()) {

                Text(

                    text = "Analytics",

                    fontSize = 28.sp,

                    fontWeight = FontWeight.Black,

                    color = MaterialTheme.colorScheme.onBackground

                )

                Text(

                    text = "Your hydration insights over time",

                    fontSize = 14.sp,

                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)

                )

            }

        }



        // Summary Statistics Cards

        item {

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.spacedBy(12.dp)

            ) {

                // Daily Average

                Card(

                    modifier = Modifier.weight(1f),

                    shape = RoundedCornerShape(20.dp),

                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),

                    colors = CardDefaults.cardColors(

                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)

                    )

                ) {

                    Column(

                        modifier = Modifier.padding(16.dp),

                        horizontalAlignment = Alignment.Start

                    ) {

                        Text(

                            "Daily Average",

                            fontSize = 12.sp,

                            fontWeight = FontWeight.Bold,

                            color = MaterialTheme.colorScheme.primary

                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(

                            "${state.dailyAverageMl} ml",

                            fontSize = 24.sp,

                            fontWeight = FontWeight.Black,

                            color = MaterialTheme.colorScheme.onBackground

                        )

                    }

                }



                // Hydration Score

                Card(

                    modifier = Modifier.weight(1f),

                    shape = RoundedCornerShape(20.dp),

                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),

                    colors = CardDefaults.cardColors(

                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)

                    )

                ) {

                    Column(

                        modifier = Modifier.padding(16.dp),

                        horizontalAlignment = Alignment.Start

                    ) {

                        Text(

                            "Hydration Score",

                            fontSize = 12.sp,

                            fontWeight = FontWeight.Bold,

                            color = MaterialTheme.colorScheme.secondary

                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(

                            "${state.hydrationScore}/100",

                            fontSize = 24.sp,

                            fontWeight = FontWeight.Black,

                            color = MaterialTheme.colorScheme.onBackground

                        )

                    }

                }

            }

        }



        // Weekly Trends Chart Card

        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(24.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

            ) {

                Column(

                    modifier = Modifier.padding(20.dp),

                    horizontalAlignment = Alignment.Start

                ) {

                    Text(

                        text = "Weekly Consumption Trends",

                        fontWeight = FontWeight.Bold,

                        fontSize = 16.sp,

                        color = MaterialTheme.colorScheme.onSurface

                    )

                    Spacer(modifier = Modifier.height(24.dp))



                    // Chart Container with simulated Goal Baseline Line

                    Box(

                        modifier = Modifier

                            .fillMaxWidth()

                            .height(180.dp)

                    ) {

                        // 1. Goal Baseline Overlay Line at 2000ml (about 2/3 of 3000ml maximum)

                        val baselineGoalY = 180.dp * (1f - (2000f / 3000f))

                        

                        Box(

                            modifier = Modifier

                                .fillMaxWidth()

                                .padding(horizontal = 8.dp)

                                .offset(y = baselineGoalY)

                                .height(1.dp)

                                .background(

                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)

                                )

                        ) {

                            Text(

                                text = "Goal: 2L",

                                fontSize = 8.sp,

                                fontWeight = FontWeight.Bold,

                                color = MaterialTheme.colorScheme.secondary,

                                modifier = Modifier

                                    .align(Alignment.TopEnd)

                                    .offset(y = (-10).dp)

                            )

                        }



                        // 2. Bar charts columns

                        Row(

                            modifier = Modifier.fillMaxSize(),

                            horizontalArrangement = Arrangement.SpaceBetween,

                            verticalAlignment = Alignment.Bottom

                        ) {

                            state.weeklyChartData.forEach { (day, amount) ->

                                val maxAmount = 3000f

                                val heightRatio = (amount.toFloat() / maxAmount).coerceIn(0.05f, 1.0f)

                                

                                val barBrush = if (amount >= 2000) {

                                    Brush.verticalGradient(

                                        colors = listOf(

                                            MaterialTheme.colorScheme.secondary,

                                            MaterialTheme.colorScheme.primary

                                        )

                                    )

                                } else {

                                    Brush.verticalGradient(

                                        colors = listOf(

                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),

                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)

                                        )

                                    )

                                }



                                Column(

                                    horizontalAlignment = Alignment.CenterHorizontally,

                                    modifier = Modifier.weight(1f)

                                ) {

                                    Text(

                                        text = if (amount > 0) "${amount}ml" else "0",

                                        fontSize = 9.sp,

                                        fontWeight = FontWeight.Bold,

                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Box(

                                        modifier = Modifier

                                            .width(16.dp)

                                            .fillMaxHeight(heightRatio)

                                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))

                                            .background(barBrush)

                                            .border(

                                                BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f)),

                                                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)

                                            )

                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(

                                        text = day,

                                        fontSize = 11.sp,

                                        fontWeight = FontWeight.Bold,

                                        color = MaterialTheme.colorScheme.onSurface

                                    )

                                }

                            }

                        }

                    }

                }

            }

        }



        // Monthly Consistency Heatmap Card

        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(24.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

            ) {

                Column(

                    modifier = Modifier.padding(20.dp),

                    horizontalAlignment = Alignment.Start

                ) {

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Text(

                            text = "Consistency Map",

                            fontWeight = FontWeight.Bold,

                            fontSize = 16.sp,

                            color = MaterialTheme.colorScheme.onSurface

                        )

                        Box(

                            modifier = Modifier

                                .clip(RoundedCornerShape(6.dp))

                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))

                                .padding(horizontal = 8.dp, vertical = 4.dp)

                        ) {

                            Text(

                                text = "${state.consistencyPercent}% consistent",

                                fontSize = 11.sp,

                                fontWeight = FontWeight.Bold,

                                color = MaterialTheme.colorScheme.secondary

                            )

                        }

                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(

                        text = "Visualizing goal completions over the last 28 days",

                        fontSize = 12.sp,

                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                    )

                    Spacer(modifier = Modifier.height(20.dp))



                    // Draw a grid of last 28 days

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.spacedBy(8.dp)

                    ) {

                        for (w in 0..3) {

                            Column(

                                modifier = Modifier.weight(1f),

                                verticalArrangement = Arrangement.spacedBy(8.dp),

                                horizontalAlignment = Alignment.CenterHorizontally

                            ) {

                                for (d in 0..6) {

                                    val index = w * 7 + d

                                    val cal = Calendar.getInstance()

                                    cal.add(Calendar.DATE, -index)

                                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

                                    val reachedGoal = state.monthlyHeatmapData[dateStr] ?: false

                                    

                                    Box(

                                        modifier = Modifier

                                            .size(26.dp)

                                            .clip(RoundedCornerShape(6.dp))

                                            .background(

                                                if (reachedGoal) MaterialTheme.colorScheme.secondary 

                                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)

                                            )

                                            .border(

                                                BorderStroke(

                                                    0.5.dp, 

                                                    if (reachedGoal) Color.Transparent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)

                                                ),

                                                RoundedCornerShape(6.dp)

                                            )

                                    )

                                }

                            }

                        }

                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.Center,

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.secondary))

                        Spacer(modifier = Modifier.width(6.dp))

                        Text("Goal Met", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                        Spacer(modifier = Modifier.width(20.dp))

                        Box(

                            modifier = Modifier

                                .size(10.dp)

                                .clip(RoundedCornerShape(2.dp))

                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                                .border(BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)), RoundedCornerShape(2.dp))

                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text("Goal Missed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                    }

                }

            }

        }



        // Beverage distribution breakdown

        item {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(24.dp),

                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

            ) {

                Column(

                    modifier = Modifier.padding(20.dp),

                    horizontalAlignment = Alignment.Start

                ) {

                    Text(

                        text = "Beverage Share",

                        fontWeight = FontWeight.Bold,

                        fontSize = 16.sp,

                        color = MaterialTheme.colorScheme.onSurface

                    )

                    Spacer(modifier = Modifier.height(16.dp))



                    if (state.beverageDistribution.isEmpty()) {

                        Text(

                            text = "No logs logged to show breakdown.",

                            fontSize = 13.sp,

                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                        )

                    } else {

                        val totalLogs = state.beverageDistribution.values.sum().toFloat()

                        state.beverageDistribution.forEach { (bevName, count) ->

                            val percent = (count.toFloat() / totalLogs * 100f).toInt()

                            val matchingBev = Beverage.values().find { it.displayName == bevName }

                            val bevColor = matchingBev?.let { parseColor(it.colorHex) } ?: MaterialTheme.colorScheme.primary



                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {

                                Row(

                                    modifier = Modifier.fillMaxWidth(),

                                    horizontalArrangement = Arrangement.SpaceBetween,

                                    verticalAlignment = Alignment.CenterVertically

                                ) {

                                    Row(verticalAlignment = Alignment.CenterVertically) {

                                        Box(

                                            modifier = Modifier

                                                .size(24.dp)

                                                .clip(RoundedCornerShape(6.dp))

                                                .background(bevColor.copy(alpha = 0.15f)),

                                            contentAlignment = Alignment.Center

                                        ) {

                                            Icon(

                                                imageVector = getBeverageIcon(bevName),

                                                contentDescription = null,

                                                tint = bevColor,

                                                modifier = Modifier.size(13.dp)

                                            )

                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(

                                            bevName, 

                                            fontSize = 13.sp, 

                                            fontWeight = FontWeight.Bold,

                                            color = MaterialTheme.colorScheme.onSurface

                                        )

                                    }

                                    Text(

                                        "$percent%", 

                                        fontSize = 13.sp, 

                                        fontWeight = FontWeight.Black, 

                                        color = bevColor

                                    )

                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(

                                    progress = count.toFloat() / totalLogs,

                                    modifier = Modifier

                                        .fillMaxWidth()

                                        .height(8.dp)

                                        .clip(RoundedCornerShape(4.dp)),

                                    color = bevColor,

                                    trackColor = bevColor.copy(alpha = 0.08f)

                                )

                            }

                        }

                    }

                }

            }

        }



        // PDF Generation Trigger Button

        item {

            Button(

                onClick = {

                    exportPdfReport(context, state.dailyAverageMl, state.hydrationScore, state.consistencyPercent)

                },

                modifier = Modifier

                    .fillMaxWidth()

                    .height(48.dp),

                shape = RoundedCornerShape(12.dp),

                colors = ButtonDefaults.buttonColors(

                    containerColor = MaterialTheme.colorScheme.primary

                )

            ) {

                Icon(

                    imageVector = Icons.Default.PictureAsPdf,

                    contentDescription = null,

                    modifier = Modifier.size(18.dp)

                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Export Monthly PDF Report", fontWeight = FontWeight.Bold, fontSize = 13.sp)

            }

        }



        item {

            Spacer(modifier = Modifier.height(64.dp))

        }

    }

}



private fun exportPdfReport(context: Context, dailyAverage: Int, score: Int, consistency: Int) {

    try {

        val pdfDocument = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(300, 400, 1).create()

        val page = pdfDocument.startPage(pageInfo)



        val canvas = page.canvas

        val paint = Paint()



        // Write title

        paint.textSize = 18f

        paint.isFakeBoldText = true

        canvas.drawText("Stay Hydrated Report", 20f, 40f, paint)



        // Date

        paint.textSize = 10f

        paint.isFakeBoldText = false

        val dateStr = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

        canvas.drawText("Generated on: $dateStr", 20f, 60f, paint)



        canvas.drawLine(20f, 75f, 280f, 75f, paint)



        // Metrics

        paint.textSize = 12f

        canvas.drawText("Daily Average Intake: $dailyAverage ml", 20f, 100f, paint)

        canvas.drawText("Hydration Consistency: $consistency%", 20f, 130f, paint)

        canvas.drawText("Overall Hydration Score: $score / 100", 20f, 160f, paint)



        paint.textSize = 10f

        canvas.drawText("Keep up the good habits to optimize energy levels", 20f, 220f, paint)

        canvas.drawText("and general wellness with smart hydration.", 20f, 235f, paint)



        pdfDocument.finishPage(page)



        // Save PDF to Downloads

        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val file = File(directory, "HydraFlow_AI_Report_${System.currentTimeMillis()}.pdf")

        

        pdfDocument.writeTo(FileOutputStream(file))

        pdfDocument.close()



        Toast.makeText(context, "PDF Report saved to Downloads: ${file.name}", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {

        Toast.makeText(context, "Failed to export PDF: ${e.message}", Toast.LENGTH_SHORT).show()

    }

}



