package com.example.raktasewa

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.raktasewa.data.BloodBankResponse
import com.example.raktasewa.ui.screens.FindingBloodBanksScreen
import com.example.raktasewa.ui.screens.LanguageSelectionScreen
import com.example.raktasewa.ui.screens.NearbyBloodBanksScreen
import com.example.raktasewa.ui.screens.SelectBloodGroupScreen
import com.example.raktasewa.ui.theme.RaktaSewaTheme
import com.example.raktasewa.viewmodel.BloodBankUiState
import com.example.raktasewa.viewmodel.BloodBankViewModel
import androidx.activity.compose.BackHandler

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
                    val bloodBankViewModel: BloodBankViewModel = viewModel()

                    var currentScreen by remember { mutableStateOf("language_selection") }
                    var selectedLanguage by remember { mutableStateOf("") }
                    var selectedBloodGroup by remember { mutableStateOf("") }
                    var fetchedBloodBanks by remember { mutableStateOf<List<BloodBankResponse>>(emptyList()) }
                    var fetchError by remember { mutableStateOf<String?>(null) }

                    // Observe the ViewModel's UI state
                    val uiState by bloodBankViewModel.uiState.collectAsState()

                    // When blood group is selected and we navigate to the loading screen,
                    // kick off the API call immediately
                    LaunchedEffect(currentScreen) {
                        if (currentScreen == "finding_blood_banks") {
                            bloodBankViewModel.fetchBloodBanks(selectedBloodGroup)
                        }
                    }

                    // React to ViewModel state changes while on the loading screen
                    LaunchedEffect(uiState) {
                        if (currentScreen == "finding_blood_banks") {
                            when (val state = uiState) {
                                is BloodBankUiState.Success -> {
                                    fetchedBloodBanks = state.bloodBanks
                                    fetchError = null
                                    // Small delay so user sees the loading animation
                                    kotlinx.coroutines.delay(1500)
                                    currentScreen = "nearby_blood_banks"
                                    bloodBankViewModel.resetState()
                                }
                                is BloodBankUiState.Error -> {
                                    fetchError = state.message
                                    fetchedBloodBanks = emptyList()
                                    // Still navigate to show error state
                                    kotlinx.coroutines.delay(1500)
                                    currentScreen = "nearby_blood_banks"
                                    bloodBankViewModel.resetState()
                                }
                                else -> { /* Loading or Idle — stay on loading screen */ }
                            }
                        }
                    }

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
                                    language = selectedLanguage,
                                    onBackClick = {
                                        currentScreen = "language_selection"
                                    },
                                    onFindNearbyClick = { bloodGroup ->
                                        selectedBloodGroup = bloodGroup
                                        fetchedBloodBanks = emptyList()
                                        fetchError = null
                                        currentScreen = "finding_blood_banks"
                                    }
                                )
                            }
                            "finding_blood_banks" -> {
                                BackHandler {
                                    bloodBankViewModel.resetState()
                                    currentScreen = "select_blood_group"
                                }
                                FindingBloodBanksScreen(
                                    selectedBloodGroup = selectedBloodGroup,
                                    language = selectedLanguage
                                )
                            }
                            "nearby_blood_banks" -> {
                                NearbyBloodBanksScreen(
                                    selectedBloodGroup = selectedBloodGroup,
                                    language = selectedLanguage,
                                    bloodBanks = fetchedBloodBanks,
                                    errorMessage = fetchError,
                                    onBackClick = {
                                        currentScreen = "select_blood_group"
                                    },
                                    onRetryClick = {
                                        fetchedBloodBanks = emptyList()
                                        fetchError = null
                                        currentScreen = "finding_blood_banks"
                                    }
                                )
                            }
                            else -> {
                                // Fallback — prevents blank screen during animated transitions
                                LanguageSelectionScreen(
                                    onLanguageSelected = { lang ->
                                        selectedLanguage = lang
                                        currentScreen = "select_blood_group"
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