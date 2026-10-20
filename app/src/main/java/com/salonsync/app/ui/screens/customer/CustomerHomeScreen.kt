package com.salonsync.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salonsync.app.data.model.Service
import com.salonsync.app.ui.theme.CardBorder
import com.salonsync.app.ui.theme.SlateDark
import com.salonsync.app.ui.theme.SoftGold
import com.salonsync.app.ui.theme.SurfaceDark
import com.salonsync.app.ui.theme.TextLight
import com.salonsync.app.ui.theme.TextMuted
import com.salonsync.app.ui.theme.WarmGold
import com.salonsync.app.ui.viewmodel.CustomerUiState
import com.salonsync.app.ui.viewmodel.CustomerViewModel

@Composable
fun CustomerHomeScreen(
    viewModel: CustomerViewModel,
    uiState: CustomerUiState,
    onSelectServiceForBooking: (Service) -> Unit
) {
    val categories = listOf("All", "Hair", "Skin", "Nails", "Spa")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Glow Salon",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftGold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Text("Sector 5, Main Street", fontSize = 12.sp, color = TextMuted)
                }
            }

            // Mode Toggle (In-Salon vs At-Home)
            Row(
                modifier = Modifier
                    .background(SurfaceDark, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(if (uiState.bookingMode == "inSalon") WarmGold else SurfaceDark, RoundedCornerShape(8.dp))
                        .clickable { viewModel.selectMode("inSalon") }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Store, contentDescription = null, tint = if (uiState.bookingMode == "inSalon") SlateDark else TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salon", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (uiState.bookingMode == "inSalon") SlateDark else TextMuted)
                    }
                }

                Box(
                    modifier = Modifier
                        .background(if (uiState.bookingMode == "atHome") WarmGold else SurfaceDark, RoundedCornerShape(8.dp))
                        .clickable { viewModel.selectMode("atHome") }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = if (uiState.bookingMode == "atHome") SlateDark else TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (uiState.bookingMode == "atHome") SlateDark else TextMuted)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search services (e.g. Haircut, Facial)...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = WarmGold) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WarmGold,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = TextLight,
                unfocusedTextColor = TextLight
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Categories Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = uiState.selectedCategory == cat
                Box(
                    modifier = Modifier
                        .background(if (isSelected) WarmGold else SurfaceDark, shape = RoundedCornerShape(20.dp))
                        .clickable { viewModel.selectCategory(cat) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) SlateDark else TextLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filtered Service Cards
        val filteredServices = uiState.services.filter { s ->
            val matchCat = uiState.selectedCategory == "All" || s.category.equals(uiState.selectedCategory, ignoreCase = true)
            val matchSearch = s.name.contains(uiState.searchQuery, ignoreCase = true)
            val matchMode = if (uiState.bookingMode == "atHome") s.modes.atHome else s.modes.inSalon
            matchCat && matchSearch && matchMode
        }

        if (filteredServices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No services available for this selection", color = TextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredServices) { service ->
                    ServiceCard(service = service, onBookNow = {
                        viewModel.selectService(service)
                        onSelectServiceForBooking(service)
                    })
                }
            }
        }
    }
}

@Composable
fun ServiceCard(service: Service, onBookNow: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardBorder.let { androidx.compose.foundation.BorderStroke(1.dp, it) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Text(
                    text = service.description,
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⏱ ${service.durationMin} mins",
                        fontSize = 12.sp,
                        color = SoftGold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = service.priceFormatted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarmGold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onBookNow,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WarmGold)
            ) {
                Text("Book", fontWeight = FontWeight.Bold, color = SlateDark, fontSize = 13.sp)
            }
        }
    }
}
