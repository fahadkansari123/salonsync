package com.salonsync.app.ui.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.salonsync.app.ui.theme.AccentGreen
import com.salonsync.app.ui.theme.AccentRed
import com.salonsync.app.ui.theme.CardBorder
import com.salonsync.app.ui.theme.SlateDark
import com.salonsync.app.ui.theme.SoftGold
import com.salonsync.app.ui.theme.SurfaceDark
import com.salonsync.app.ui.theme.TextLight
import com.salonsync.app.ui.theme.TextMuted
import com.salonsync.app.ui.theme.WarmGold
import com.salonsync.app.ui.viewmodel.StaffUiState
import com.salonsync.app.ui.viewmodel.StaffViewModel

@Composable
fun StaffScheduleScreen(
    viewModel: StaffViewModel,
    uiState: StaffUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(16.dp)
    ) {
        Text("Stylist Schedule", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SoftGold)
        Text("Your assigned jobs for ${uiState.selectedDate}", fontSize = 12.sp, color = TextMuted)

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.todayBookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No assigned appointments for today", color = TextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.todayBookings) { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = CardBorder.let { androidx.compose.foundation.BorderStroke(1.dp, it) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val statusLower = booking.status.lowercase()
                            val statusColor = when (statusLower) {
                                "pending" -> WarmGold
                                "confirmed" -> AccentGreen
                                "completed" -> SoftGold
                                "cancelled" -> AccentRed
                                else -> WarmGold
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(booking.startTime, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WarmGold)
                                Text(booking.status.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text("Customer: ${booking.customerName}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextLight)
                            Text("Service: ${booking.services.firstOrNull()?.name ?: "Service"}", fontSize = 13.sp, color = SoftGold)

                            if (booking.mode == "atHome" && booking.addressSnapshot != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("📍 ${booking.addressSnapshot.line1}, ${booking.addressSnapshot.pincode}", fontSize = 11.sp, color = TextMuted)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (statusLower == "confirmed") {
                                Button(
                                    onClick = { viewModel.updateStatus(booking.id, "completed") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                                ) {
                                    Text("Mark Completed", fontWeight = FontWeight.Bold, color = TextLight)
                                }
                            } else if (statusLower == "pending") {
                                Text("⏳ Awaiting admin confirmation", fontSize = 11.sp, color = WarmGold, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
