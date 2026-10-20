package com.salonsync.app.ui.screens.customer

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.salonsync.app.ui.viewmodel.CustomerUiState

@Composable
fun CustomerBookingsScreen(
    uiState: CustomerUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(16.dp)
    ) {
        Text("My Appointments", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SoftGold)
        Text("Track appointment approval & status", fontSize = 12.sp, color = TextMuted)

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.myBookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No bookings found", color = TextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.myBookings) { booking ->
                    BookingItemCard(booking = booking)
                }
            }
        }
    }
}

@Composable
fun BookingItemCard(booking: Booking) {
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
                    Text(
                        text = booking.services.firstOrNull()?.name ?: "Salon Service",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Text(
                        text = if (booking.mode == "atHome") "🏡 At-Home Service" else "💈 In-Salon Appointment",
                        fontSize = 12.sp,
                        color = SoftGold
                    )
                }

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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📅 ${booking.date} at ${booking.startTime} • Stylist: ${booking.staffName}",
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (statusLower) {
                        "pending" -> "⏳ Awaiting admin confirmation"
                        "confirmed" -> "✅ Slot confirmed by salon"
                        "completed" -> "✨ Completed"
                        "cancelled" -> "❌ Cancelled"
                        else -> ""
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )

                Text(
                    text = booking.pricing.totalFormatted,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmGold
                )
            }
        }
    }
}
