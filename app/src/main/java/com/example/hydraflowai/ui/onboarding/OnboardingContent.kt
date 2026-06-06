package com.example.hydraflowai.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydraflowai.data.weather.ActivityLevel

@Composable
fun OnboardingScreen(
    onComplete: (Float, ActivityLevel) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var weight by remember { mutableFloatStateOf(70f) }
    var selectedActivity by remember { mutableStateOf(ActivityLevel.ACTIVE) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
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
                if (step == 1) {
                    Text(
                        text = "Tell us about yourself",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "We use your weight to calculate your personalized baseline daily hydration needs.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = "${weight.toInt()} kg",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Slider(
                        value = weight,
                        onValueChange = { weight = it },
                        valueRange = 30f..150f,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { step = 2 },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Next Step")
                    }
                } else {
                    Text(
                        text = "What is your activity level?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Active lifestyles require additional hydration adjustments to prevent exhaustion.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    ActivityLevel.values().forEach { level ->
                        val selected = selectedActivity == level
                        OutlinedButton(
                            onClick = { selectedActivity = level },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(
                                enabled = selected
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .height(56.dp)
                        ) {
                            Text(
                                text = level.displayName,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { step = 1 },
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Text("Back")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { onComplete(weight, selectedActivity) },
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Text("Finish")
                        }
                    }
                }
            }
        }
    }
}
