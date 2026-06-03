package com.example.raktasewa

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.raktasewa.ui.screens.LanguageSelectionScreen
import com.example.raktasewa.ui.screens.SelectBloodGroupScreen
import com.example.raktasewa.ui.screens.FindingBloodBanksScreen
import com.example.raktasewa.ui.screens.NearbyBloodBanksScreen
import androidx.activity.compose.BackHandler
import com.example.raktasewa.ui.theme.RaktaSewaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RaktaSewaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("language_selection") }
                    var selectedLanguage by remember { mutableStateOf("") }
                    var selectedBloodGroup by remember { mutableStateOf("") }
                    val context = LocalContext.current

                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn().togetherWith(fadeOut())
                        },
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            "language_selection" -> {
                                LanguageSelectionScreen(
                                    onLanguageSelected = { lang ->
                                        selectedLanguage = lang
                                        currentScreen = "select_blood_group"
                                    }
                                )
                            }
                            "select_blood_group" -> {
                                SelectBloodGroupScreen(
                                    onBackClick = {
                                        currentScreen = "language_selection"
                                    },
                                    onProfileClick = {
                                        Toast.makeText(context, "Profile clicked", Toast.LENGTH_SHORT).show()
                                    },
                                    onFindNearbyClick = { bloodGroup ->
                                        selectedBloodGroup = bloodGroup
                                        currentScreen = "finding_blood_banks"
                                    }
                                )
                            }
                            "finding_blood_banks" -> {
                                BackHandler {
                                    currentScreen = "select_blood_group"
                                }
                                LaunchedEffect(Unit) {
                                    kotlinx.coroutines.delay(4000)
                                    currentScreen = "nearby_blood_banks"
                                }
                                FindingBloodBanksScreen(
                                    selectedBloodGroup = selectedBloodGroup
                                )
                            }
                            "nearby_blood_banks" -> {
                                NearbyBloodBanksScreen(
                                    selectedBloodGroup = selectedBloodGroup,
                                    onBackClick = {
                                        currentScreen = "select_blood_group"
                                    },
                                    onProfileClick = {
                                        Toast.makeText(context, "Profile clicked", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}