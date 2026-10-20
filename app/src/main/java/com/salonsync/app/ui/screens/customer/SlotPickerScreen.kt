package com.salonsync.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salonsync.app.data.model.SlotState
import com.salonsync.app.data.model.SlotStatus
import com.salonsync.app.data.model.Staff
import com.salonsync.app.ui.theme.AccentGreen
import com.salonsync.app.ui.theme.AccentRed
import com.salonsync.app.ui.theme.CardBorder
import com.salonsync.app.ui.theme.SlateDark
import com.salonsync.app.ui.theme.SoftGold
import com.salonsync.app.ui.theme.SurfaceDark
import com.salonsync.app.ui.theme.TextLight
import com.salonsync.app.ui.theme.TextMuted
import com.salonsync.app.ui.theme.WarmGold
import com.salonsync.app.ui.viewmodel.CustomerUiState
import com.salonsync.app.ui.viewmodel.CustomerViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun SlotPickerScreen(
    viewModel: CustomerViewModel,
    uiState: CustomerUiState,
    onNavigateToSummary: () -> Unit
) {
    val service = uiState.selectedService
    val dates = (0..14).map { LocalDate.now().plusDays(it.toLong()) }
    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(16.dp)
    ) {
        // Selected Service Card
        if (service != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(service.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SoftGold)
                        Text("${service.durationMin} mins • Mode: ${uiState.bookingMode}", fontSize = 12.sp, color = TextMuted)
                    }
                    Text(service.priceFormatted, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WarmGold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date Selector
        Text("Select Date", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextLight)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(dates) { date ->
                val dateStr = date.toString()
                val isSelected = uiState.selectedDate == dateStr

                Box(
                    modifier = Modifier
                        .background(if (isSelected) WarmGold else SurfaceDark, shape = RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectDate(dateStr) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (date == LocalDate.now()) "Today" else date.format(DateTimeFormatter.ofPattern("EEE")),
                            fontSize = 11.sp,
                            color = if (isSelected) SlateDark else TextMuted
                        )
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("dd MMM")),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) SlateDark else TextLight
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Staff Selection
        Text("Preferred Stylist", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextLight)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                val isSelected = uiState.selectedStaff == null
                Box(
                    modifier = Modifier
                        .background(if (isSelected) WarmGold else SurfaceDark, shape = RoundedCornerShape(20.dp))
                        .clickable { viewModel.selectStaff(null) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Any Available", fontSize = 12.sp, color = if (isSelected) SlateDark else TextLight)
                }
            }

            items(uiState.staffList) { staff ->
                val isSelected = uiState.selectedStaff?.id == staff.id
                Box(
                    modifier = Modifier
                        .background(if (isSelected) WarmGold else SurfaceDark, shape = RoundedCornerShape(20.dp))
                        .clickable { viewModel.selectStaff(staff) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(staff.name, fontSize = 12.sp, color = if (isSelected) SlateDark else TextLight)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Slot Grid Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Available Slots (${uiState.selectedDate})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextLight)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(AccentGreen, RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Free", fontSize = 10.sp, color = TextMuted)
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(8.dp).background(AccentRed, RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Booked", fontSize = 10.sp, color = TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Slots Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(uiState.slots) { slot ->
                val isSelected = uiState.selectedSlot?.timeKey == slot.timeKey
                val isAvailable = slot.status == SlotStatus.AVAILABLE

                val bgColor = when {
                    isSelected -> WarmGold
                    !isAvailable -> SurfaceDark.copy(alpha = 0.5f)
                    else -> SurfaceDark
                }

                val borderColor = when {
                    isSelected -> WarmGold
                    slot.status == SlotStatus.BOOKED -> AccentRed.copy(alpha = 0.4f)
                    else -> CardBorder
                }

                val textColor = when {
                    isSelected -> SlateDark
                    slot.status == SlotStatus.BOOKED || slot.status == SlotStatus.PAST -> TextMuted
                    else -> TextLight
                }

                Box(
                    modifier = Modifier
                        .background(bgColor, shape = RoundedCornerShape(10.dp))
                        .border(1.dp, borderColor, shape = RoundedCornerShape(10.dp))
                        .clickable(enabled = isAvailable) { viewModel.selectSlot(slot) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = slot.displayTime,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                        if (slot.status == SlotStatus.BOOKED) {
                            Text("Booked", fontSize = 9.sp, color = AccentRed)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNavigateToSummary,
            enabled = uiState.selectedSlot != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = WarmGold,
                disabledContainerColor = SurfaceDark
            )
        ) {
            Text(
                text = if (uiState.selectedSlot != null) "Proceed to Confirmation" else "Select a Time Slot",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (uiState.selectedSlot != null) SlateDark else TextMuted
            )
        }
    }
}
