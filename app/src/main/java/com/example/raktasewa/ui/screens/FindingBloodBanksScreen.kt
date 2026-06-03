package com.example.raktasewa.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raktasewa.ui.theme.RaktaSewaTheme
import kotlinx.coroutines.delay

data class StatusText(val main: String, val sub: String)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FindingBloodBanksScreen(
    selectedBloodGroup: String,
    modifier: Modifier = Modifier
) {
    // 1. Text switching transition data
    val statuses = remember(selectedBloodGroup) {
        listOf(
            StatusText("Finding nearest blood banks...", "Searching within 20km..."),
            StatusText("Checking availability...", "Verifying $selectedBloodGroup stock levels..."),
            StatusText("Optimizing routes...", "Calculating arrival time for retrieval..."),
            StatusText("Syncing donor data...", "Connecting to regional health network...")
        )
    }
    var currentStatusIndex by remember { mutableStateOf(0) }

    LaunchedEffect(statuses) {
        while (true) {
            delay(4000)
            currentStatusIndex = (currentStatusIndex + 1) % statuses.size
        }
    }

    // 2. Ripple pulse infinite transitions
    val rippleTransition = rememberInfiniteTransition(label = "ripple")
    val rippleProgress1 by rippleTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple1"
    )

    // Ripple 2 is shifted by 180 degrees (1.5 seconds delay in a 3-second cycle)
    val rippleProgress2 = (rippleProgress1 + 0.5f) % 1.0f

    // 3. Central Blood Drop Pulse Animation
    val pulseTransition = rememberInfiniteTransition(label = "bloodDropPulse")
    val pulseProgress by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseProgress"
    )
    val pulseScale = 1.0f + 0.1f * pulseProgress
    val pulseAlpha = 1.0f - 0.2f * pulseProgress

    // 4. Secure Connection Dot Animation
    val dotTransition = rememberInfiniteTransition(label = "dotPulse")
    val dotAlpha by dotTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Decorative background icon (share_location) with very low opacity
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.03f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShareLocation,
                contentDescription = null,
                modifier = Modifier.size(400.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main content pushes down the footer
            Spacer(modifier = Modifier.weight(1f))

            // Central Animation Container (Pulsing Ripples + Center Drop)
            Box(
                modifier = Modifier.size(192.dp),
                contentAlignment = Alignment.Center
            ) {
                // Ripple 1
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r1Scale = 0.8f + (2.5f - 0.8f) * rippleProgress1
                    val r1Alpha = 0.5f * (1f - rippleProgress1)
                    val radius = (size.minDimension / 2) * r1Scale
                    drawCircle(
                        color = primaryColor.copy(alpha = r1Alpha),
                        radius = radius,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Ripple 2
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r2Scale = 0.8f + (2.5f - 0.8f) * rippleProgress2
                    val r2Alpha = 0.5f * (1f - rippleProgress2)
                    val radius = (size.minDimension / 2) * r2Scale
                    drawCircle(
                        color = primaryColor.copy(alpha = r2Alpha),
                        radius = radius,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Central Circle Card for Blood Drop Icon
                Surface(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(pulseScale),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    ),
                    shadowElevation = 2.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bloodtype,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier
                                .size(48.dp)
                                .alpha(pulseAlpha)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Textual feedback
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp), // Fixed height to prevent layout shifting
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = statuses[currentStatusIndex],
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "status_transition"
                ) { status ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = status.main,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 20.sp,
                                lineHeight = 28.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = status.sub,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Indeterminate Progress Indicator (matching HTML style)
            Box(
                modifier = Modifier
                    .width(192.dp)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = CircleShape
                    )
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    color = primaryColor,
                    trackColor = Color.Transparent
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Branding Footer
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "VITALFLOW",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .alpha(dotAlpha)
                            .background(primaryColor, shape = CircleShape)
                    )
                    Text(
                        text = "Secure Connection Established",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FindingBloodBanksScreenPreview() {
    RaktaSewaTheme {
        FindingBloodBanksScreen(selectedBloodGroup = "O+")
    }
}
