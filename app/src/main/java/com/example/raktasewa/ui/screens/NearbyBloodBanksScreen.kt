package com.example.raktasewa.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raktasewa.data.BloodBankResponse
import com.example.raktasewa.ui.theme.PrimaryFixed
import com.example.raktasewa.ui.theme.RaktaSewaTheme

data class BloodBank(
    val name: String,
    val distance: Double?,
    val stockStatus: StockStatus,
    val units: Int,
    val phone: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

enum class StockStatus(val label: String, val color: Color, val dotColor: Color) {
    HIGH_STOCK("High Stock", Color(0xFF2E7D32), Color(0xFF2E7D32)),
    LOW_STOCK("Low Stock", Color(0xFFE65100), Color(0xFFE65100)),
    CRITICAL("Critical", Color(0xFFB7102A), Color(0xFFB7102A))
}

/**
 * Convert API response models to display-ready BloodBank objects.
 */
private fun BloodBankResponse.toBloodBank(): BloodBank {
    val qty = quantity?.toInt() ?: 0
    val status = when {
        qty >= 10 -> StockStatus.HIGH_STOCK
        qty >= 3 -> StockStatus.LOW_STOCK
        else -> StockStatus.CRITICAL
    }
    return BloodBank(
        name = name,
        distance = null, // API doesn't provide distance; could compute client-side
        stockStatus = status,
        units = qty,
        phone = contact ?: "",
        address = address ?: "",
        latitude = latitude,
        longitude = longitude
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyBloodBanksScreen(
    selectedBloodGroup: String,
    language: String,
    bloodBanks: List<BloodBankResponse>,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterKey by remember { mutableStateOf("all_banks") }

    val filterKeys = remember {
        listOf("all_banks", "nearest_first", "open_now", "emergency_priority")
    }

    // Convert API response to display models
    val allBanks = remember(bloodBanks) {
        bloodBanks.map { it.toBloodBank() }
    }

    // Filter and Sort banks based on selection
    val filteredBanks = remember(searchQuery, selectedFilterKey, allBanks) {
        var list = allBanks.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.address.contains(searchQuery, ignoreCase = true)
        }

        when (selectedFilterKey) {
            "nearest_first" -> {
                list = list.sortedBy { it.distance ?: Double.MAX_VALUE }
            }
            "open_now" -> {
                // Show banks with stock available
                list = list.filter { it.units > 0 }
            }
            "emergency_priority" -> {
                // Show Critical and Low stock first
                list = list.sortedWith(compareBy<BloodBank> {
                    when (it.stockStatus) {
                        StockStatus.CRITICAL -> 0
                        StockStatus.LOW_STOCK -> 1
                        StockStatus.HIGH_STOCK -> 2
                    }
                })
            }
        }
        list
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🩸",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = "RaktaSewa",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        // Error state — show error message with retry button
        if (errorMessage != null && bloodBanks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "⚠️",
                        fontSize = 48.sp
                    )
                    Text(
                        text = Loc.t("error_title", language),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onRetryClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Loc.t("retry", language),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
            return@Scaffold
        }

        // Empty state — no banks found (but no error)
        if (bloodBanks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(text = "🩸", fontSize = 48.sp)
                    Text(
                        text = Loc.t("no_banks_found", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = Loc.t("no_banks_sub", language),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                    OutlinedButton(
                        onClick = onRetryClick,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(Loc.t("retry", language))
                    }
                }
            }
            return@Scaffold
        }

        // Success state — show the blood bank list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar Item
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = Loc.t("search_placeholder", language),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )
            }

            // Quick Filter Chips Item
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filterKeys) { filterKey ->
                        val isSelected = selectedFilterKey == filterKey
                        val containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                        val textColor = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedFilterKey = filterKey },
                            color = containerColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = Loc.t(filterKey, language),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = textColor,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Results count badge
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = PrimaryFixed,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Loc.t("results_found", language)
                                    .replace("{count}", filteredBanks.size.toString())
                                    .replace("{group}", selectedBloodGroup),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Loc.t("results_sub", language),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                )
                            )
                        }
                        Text(
                            text = "🩸",
                            fontSize = 36.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            // Bank Cards List
            items(filteredBanks) { bank ->
                BloodBankCard(
                    bank = bank,
                    language = language,
                    onCallClick = {
                        if (bank.phone.isNotBlank()) {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${bank.phone}"))
                            context.startActivity(intent)
                        }
                    },
                    onDirectionsClick = {
                        val geoUri = if (bank.latitude != 0.0 && bank.longitude != 0.0) {
                            "geo:${bank.latitude},${bank.longitude}?q=${Uri.encode(bank.name)}"
                        } else {
                            "geo:0,0?q=${Uri.encode(bank.name + " " + bank.address)}"
                        }
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun BloodBankCard(
    bank: BloodBank,
    language: String,
    onCallClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bank.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Show address if available
                    if (bank.address.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = bank.address,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Show phone if available
                    if (bank.phone.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = bank.phone,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Stock Info Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🩸",
                        fontSize = 12.sp
                    )
                    val stockLabelKey = when (bank.stockStatus) {
                        StockStatus.HIGH_STOCK -> "high_stock"
                        StockStatus.LOW_STOCK -> "low_stock"
                        StockStatus.CRITICAL -> "critical"
                    }
                    val stockLabel = Loc.t(stockLabelKey, language)
                    val unitLabel = if (bank.units == 1) Loc.t("unit", language) else Loc.t("units", language)
                    Text(
                        text = "$stockLabel • ${bank.units} $unitLabel",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = bank.stockStatus.color
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCallClick,
                    enabled = bank.phone.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Loc.t("call", language),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = onDirectionsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Loc.t("directions", language),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NearbyBloodBanksScreenPreview() {
    RaktaSewaTheme {
        NearbyBloodBanksScreen(
            selectedBloodGroup = "O-",
            language = "en",
            bloodBanks = listOf(
                BloodBankResponse(
                    bloodBankId = "1",
                    name = "City Central Red Cross",
                    latitude = 27.71,
                    longitude = 85.32,
                    imageUrl = null,
                    address = "Anamnagar, Kathmandu",
                    contact = "9870545658",
                    type = "O-",
                    quantity = 25.0
                )
            ),
            errorMessage = null,
            onBackClick = {},
            onRetryClick = {}
        )
    }
}
