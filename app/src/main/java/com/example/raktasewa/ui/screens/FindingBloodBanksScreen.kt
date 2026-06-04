package com.example.raktasewa.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raktasewa.ui.theme.RaktaSewaTheme
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

data class StatusText(val main: String, val sub: String)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FindingBloodBanksScreen(
    selectedBloodGroup: String,
    language: String,
    modifier: Modifier = Modifier
) {
    // 1. Text switching transition data
    val statuses = remember(selectedBloodGroup, language) {
        listOf(
            StatusText(
                Loc.t("finding_nearest", language),
                Loc.t("searching_within", language)
            ),
            StatusText(
                Loc.t("checking_availability", language),
                Loc.t("verifying_stock", language).replace("{group}", selectedBloodGroup)
            ),
            StatusText(
                Loc.t("optimizing_routes", language),
                Loc.t("calculating_arrival", language)
            ),
            StatusText(
                Loc.t("syncing_donor", language),
                Loc.t("connecting_health", language)
            )
        )
    }
    var currentStatusIndex by remember { mutableStateOf(0) }

    LaunchedEffect(statuses) {
        while (true) {
            delay(4000)
            currentStatusIndex = (currentStatusIndex + 1) % statuses.size
        }
    }

    // ─── Animation drivers ───────────────────────────────────────────────────

    val infiniteT = rememberInfiniteTransition(label = "master")

    // Orbit angles – three independent speeds
    val orbit1Angle by infiniteT.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Restart),
        label = "orbit1"
    )
    val orbit2Angle by infiniteT.animateFloat(
        initialValue = 180f, targetValue = 540f,
        animationSpec = infiniteRepeatable(tween(4800, easing = LinearEasing), RepeatMode.Restart),
        label = "orbit2"
    )
    val orbit3Angle by infiniteT.animateFloat(
        initialValue = 90f, targetValue = 450f,
        animationSpec = infiniteRepeatable(tween(6400, easing = LinearEasing), RepeatMode.Restart),
        label = "orbit3"
    )

    // Outer glow pulse
    val glowScale by infiniteT.animateFloat(
        initialValue = 0.85f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    // Core inner breathe
    val coreScale by infiniteT.animateFloat(
        initialValue = 1.0f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "coreBreath"
    )

    // Footer dot blink
    val dotAlpha by infiniteT.animateFloat(
        initialValue = 0.3f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceLow    = MaterialTheme.colorScheme.surfaceContainerLowest

    // ─── UI ──────────────────────────────────────────────────────────────────

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // ── Central orbital animation ────────────────────────────────────
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {

                // Layer 1 – Canvas: orbits, trails, glow rings
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width  / 2f
                    val cy = size.height / 2f

                    // ── Outer pulsing atmospheric glow ──────────────────────
                    val glowRadius = (size.minDimension / 2f) * glowScale
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.08f),
                                primaryColor.copy(alpha = 0.04f),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = glowRadius
                        ),
                        radius = glowRadius,
                        center = Offset(cx, cy)
                    )

                    // ── Three orbit rings (thin dashed-style circles) ────────
                    val r1 = size.minDimension * 0.37f
                    val r2 = size.minDimension * 0.27f
                    val r3 = size.minDimension * 0.17f

                    listOf(r1, r2, r3).forEach { r ->
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.10f),
                            radius = r,
                            center = Offset(cx, cy),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    // Helper: draw one orbiting drop (removed heavy trail to prevent frame drops)
                    fun drawOrbit(angleDeg: Float, orbitRadius: Float, dropRadius: Float) {
                        val angleRad = Math.toRadians(angleDeg.toDouble())

                        // Drop head: bright core + soft glow
                        val hx = cx + (orbitRadius * cos(angleRad)).toFloat()
                        val hy = cy + (orbitRadius * sin(angleRad)).toFloat()

                        // Glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.55f),
                                    primaryColor.copy(alpha = 0.0f)
                                ),
                                center = Offset(hx, hy),
                                radius = dropRadius * 2.8f
                            ),
                            radius = dropRadius * 2.8f,
                            center = Offset(hx, hy)
                        )

                        // Core
                        drawCircle(
                            color = primaryColor,
                            radius = dropRadius,
                            center = Offset(hx, hy)
                        )
                    }

                    drawOrbit(orbit1Angle, r1, dropRadius = 7.dp.toPx())
                    drawOrbit(orbit2Angle, r2, dropRadius = 5.5.dp.toPx())
                    drawOrbit(orbit3Angle, r3, dropRadius = 4.dp.toPx())
                }

                // Layer 2 – Central glowing core card
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(coreScale),
                    shape = CircleShape,
                    color = surfaceLow,
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        Brush.sweepGradient(
                            listOf(
                                primaryColor.copy(alpha = 0.0f),
                                primaryColor.copy(alpha = 0.7f),
                                primaryColor,
                                primaryColor.copy(alpha = 0.7f),
                                primaryColor.copy(alpha = 0.0f)
                            )
                        )
                    ),
                    shadowElevation = 6.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "🩸",
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Rotating status text ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = statuses[currentStatusIndex],
                    transitionSpec = {
                        fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(350))
                    },
                    label = "status_transition"
                ) { status ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = status.main,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 28.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = status.sub,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Thin animated progress bar ───────────────────────────────────
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(3.dp)
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

            // ── Branding footer ──────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🩸 RAKTASEWA",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🩸",
                        fontSize = 12.sp,
                        modifier = Modifier.alpha(dotAlpha)
                    )
                    Text(
                        text = Loc.t("secure_conn", language),
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
        FindingBloodBanksScreen(selectedBloodGroup = "O+", language = "en")
    }
}
