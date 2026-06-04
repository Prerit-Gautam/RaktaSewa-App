package com.example.raktasewa.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.raktasewa.data.BloodBankRepository
import com.example.raktasewa.data.BloodBankResponse
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Represents the state of the blood bank search operation.
 */
sealed class BloodBankUiState {
    data object Idle : BloodBankUiState()
    data object Loading : BloodBankUiState()
    data class Success(val bloodBanks: List<BloodBankResponse>) : BloodBankUiState()
    data class Error(val message: String) : BloodBankUiState()
}

/**
 * ViewModel that manages:
 * 1. Getting the user's current location via Fused Location Provider
 * 2. Calling the Raktakosh API to fetch nearby blood banks
 * 3. Exposing state for the UI to observe
 */
class BloodBankViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BloodBankRepository()
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val _uiState = MutableStateFlow<BloodBankUiState>(BloodBankUiState.Idle)
    val uiState: StateFlow<BloodBankUiState> = _uiState.asStateFlow()

    // Default fallback location: Kathmandu
    private var userLatitude: Double = 27.7172
    private var userLongitude: Double = 85.3240

    /**
     * Fetches nearby blood banks for the given blood type.
     * First attempts to get the user's current GPS location.
     * Falls back to Kathmandu coordinates if location is unavailable.
     */
    fun fetchBloodBanks(bloodType: String) {
        if (_uiState.value is BloodBankUiState.Loading) return

        _uiState.value = BloodBankUiState.Loading

        viewModelScope.launch {
            // Step 1: Try to get current location
            tryGetLocation()

            // Step 2: Call the API
            Log.d("BloodBankVM", "Fetching blood banks at ($userLatitude, $userLongitude) for type: $bloodType")

            val result = repository.getBloodBanks(
                latitude = userLatitude,
                longitude = userLongitude,
                bloodType = bloodType
            )

            result.fold(
                onSuccess = { banks ->
                    Log.d("BloodBankVM", "Success: ${banks.size} banks found")
                    _uiState.value = BloodBankUiState.Success(banks)
                },
                onFailure = { error ->
                    Log.e("BloodBankVM", "Failed to fetch blood banks", error)
                    _uiState.value = BloodBankUiState.Error(
                        error.localizedMessage ?: "Failed to fetch blood banks"
                    )
                }
            )
        }
    }

    /**
     * Attempts to get the user's current location.
     * Requires location permission to already be granted.
     */
    @SuppressLint("MissingPermission")
    private suspend fun tryGetLocation() {
        val context = getApplication<Application>()
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            Log.w("BloodBankVM", "Location permission not granted, using default coordinates")
            return
        }

        try {
            val cancellationToken = CancellationTokenSource()
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).await()

            if (location != null) {
                userLatitude = location.latitude
                userLongitude = location.longitude
                Log.d("BloodBankVM", "Got location: ($userLatitude, $userLongitude)")
            } else {
                // Try last known location as fallback
                val lastLocation = fusedLocationClient.lastLocation.await()
                if (lastLocation != null) {
                    userLatitude = lastLocation.latitude
                    userLongitude = lastLocation.longitude
                    Log.d("BloodBankVM", "Got last known location: ($userLatitude, $userLongitude)")
                } else {
                    Log.w("BloodBankVM", "No location available, using default Kathmandu coordinates")
                }
            }
        } catch (e: Exception) {
            Log.e("BloodBankVM", "Error getting location, using defaults", e)
        }
    }

    /**
     * Resets the UI state back to idle.
     */
    fun resetState() {
        _uiState.value = BloodBankUiState.Idle
    }
}
