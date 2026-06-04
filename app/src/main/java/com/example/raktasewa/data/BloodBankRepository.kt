package com.example.raktasewa.data

import android.util.Log

/**
 * Repository that abstracts the blood bank API calls.
 * Provides a clean Result-based interface for the ViewModel.
 */
class BloodBankRepository(
    private val apiService: BloodBankApiService = RetrofitClient.apiService
) {

    /**
     * Fetches blood banks near the given location for the specified blood type.
     *
     * @param latitude  User's latitude
     * @param longitude User's longitude
     * @param bloodType Blood type, e.g. "A+", "O-", "AB+"
     * @return Result wrapping a list of BloodBankResponse
     */
    suspend fun getBloodBanks(
        latitude: Double,
        longitude: Double,
        bloodType: String
    ): Result<List<BloodBankResponse>> {
        return try {
            val response = apiService.getBloodBanks(
                latitude = latitude.toString(),
                longitude = longitude.toString(),
                type = bloodType
            )
            Log.d("BloodBankRepo", "Fetched ${response.size} blood banks")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("BloodBankRepo", "Error fetching blood banks", e)
            Result.failure(e)
        }
    }
}
