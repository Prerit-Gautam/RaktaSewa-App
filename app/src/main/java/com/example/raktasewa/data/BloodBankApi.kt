package com.example.raktasewa.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Data class mapping the JSON response from
 * GET /getbloodbanks/{latitude}/{longitude}/{type}
 */
data class BloodBankResponse(
    @SerializedName("bloodBankId") val bloodBankId: String?,
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("contact") val contact: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("quantity") val quantity: Double?
)

/**
 * Retrofit API interface for the Raktakosh blood bank backend.
 */
interface BloodBankApiService {

    @GET("getbloodbanks/{latitude}/{longitude}/{type}")
    suspend fun getBloodBanks(
        @Path("latitude") latitude: String,
        @Path("longitude") longitude: String,
        @Path("type") type: String
    ): List<BloodBankResponse>
}
