package com.example.hydraflowai.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    var loggedMl by remember { mutableIntStateOf(500) }
    val goalMl = 2000
    var isSyncing by remember { mutableStateOf(false) }

    val percentage = (loggedMl.toFloat() / goalMl.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "HydraFlow AI",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$loggedMl / $goalMl ml",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = "${(percentage * 100).toInt()}% Done",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    indicatorColor = Color(0xFF34D399)
                )
            } else {
                Button(
                    onClick = {
                        loggedMl = (loggedMl + 250).coerceAtMost(3000)
                        isSyncing = true
                        // Simulating a delay for phone database sync
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            isSyncing = false
                        }, 1000)
                    },
                    modifier = Modifier
                        .height(36.dp)
                        .width(100.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0284C7))
                ) {
                    Text("+250ml", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
