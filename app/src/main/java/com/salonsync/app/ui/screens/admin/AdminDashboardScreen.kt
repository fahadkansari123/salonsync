package com.salonsync.app.ui.screens.admin

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salonsync.app.data.model.Booking
import com.salonsync.app.ui.theme.AccentGreen
import com.salonsync.app.ui.theme.AccentRed
import com.salonsync.app.ui.theme.CardBorder
import com.salonsync.app.ui.theme.SlateDark
import com.salonsync.app.ui.theme.SoftGold
import com.salonsync.app.ui.theme.SurfaceDark
import com.salonsync.app.ui.theme.TextLight
import com.salonsync.app.ui.theme.TextMuted
import com.salonsync.app.ui.theme.WarmGold
import com.salonsync.app.ui.viewmodel.AdminUiState
import com.salonsync.app.ui.viewmodel.AdminViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    uiState: AdminUiState,
    onNavigateToServices: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Admin Dashboard", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SoftGold)
                Text("Manage appointments & slot approvals", fontSize = 12.sp, color = TextMuted)
            }

            Button(
                onClick = onNavigateToServices,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WarmGold)
            ) {
                Text("Services", fontSize = 12.sp, color = SlateDark, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("All Appointments (${uiState.bookings.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextLight)
        Spacer(modifier = Modifier.height(10.dp))

        if (uiState.bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No bookings registered yet", color = TextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.bookings) { booking ->
                    AdminBookingCard(booking = booking, onUpdateStatus = { status ->
                        viewModel.updateBookingStatus(booking.id, status)
                    })
                }
            }
        }
    }
}

@Composable
fun AdminBookingCard(booking: Booking, onUpdateStatus: (String) -> Unit) {
    val statusLower = booking.status.lowercase()

    val statusColor = when (statusLower) {
        "pending" -> WarmGold
        "confirmed" -> AccentGreen
        "completed" -> SoftGold
        "cancelled" -> AccentRed
        else -> WarmGold
    }

    val statusLabel = when (statusLower) {
        "pending" -> "PENDING APPROVAL"
        "confirmed" -> "CONFIRMED"
        "completed" -> "COMPLETED"
        "cancelled" -> "CANCELLED"
        else -> booking.status.uppercase()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardBorder.let { androidx.compose.foundation.BorderStroke(1.dp, it) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(booking.customerName.ifEmpty { "Customer" }, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextLight)
                    if (booking.customerPhone.isNotBlank()) {
                        Text("📞 ${booking.customerPhone}", fontSize = 12.sp, color = TextMuted)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(booking.pricing.totalFormatted, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WarmGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Service: ${booking.services.firstOrNull()?.name ?: "Service"} (${if (booking.mode == "atHome") "🏡 Home" else "💈 Salon"})", fontSize = 13.sp, color = SoftGold)
            Text("Date & Time: ${booking.date} @ ${booking.startTime} • Staff: ${booking.staffName}", fontSize = 12.sp, color = TextMuted)

            if (booking.mode == "atHome" && booking.addressSnapshot != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text("📍 ${booking.addressSnapshot.line1}, ${booking.addressSnapshot.pincode}", fontSize = 11.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (statusLower) {
                "pending" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onUpdateStatus("confirmed") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                        ) {
                            Text("Confirm Slot", fontSize = 12.sp, color = TextLight, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onUpdateStatus("cancelled") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed)
                        ) {
                            Text("Reject Slot", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                "confirmed" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onUpdateStatus("completed") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WarmGold)
                        ) {
                            Text("Mark Completed", fontSize = 12.sp, color = SlateDark, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onUpdateStatus("cancelled") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed)
                        ) {
                            Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                "completed" -> {
                    Text("✓ Service Completed & Payment Recorded", color = SoftGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                "cancelled" -> {
                    Text("✗ Slot Cancelled / Rejected", color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
