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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun BookingSummaryScreen(
    viewModel: CustomerViewModel,
    uiState: CustomerUiState,
    onBookingSuccess: () -> Unit
) {
    val service = uiState.selectedService ?: return
    val slot = uiState.selectedSlot ?: return

    val subtotal = service.price
    val homeCharge = if (uiState.bookingMode == "atHome") uiState.settings.homeService.charge else 0L
    val tax = ((subtotal + homeCharge) * uiState.settings.taxPercent) / 100
    val total = subtotal + homeCharge + tax

    var addressLine1 by remember { mutableStateOf(uiState.homeAddress.line1) }
    var addressLine2 by remember { mutableStateOf(uiState.homeAddress.line2) }
    var pincode by remember { mutableStateOf(uiState.homeAddress.pincode) }

    if (uiState.bookingSuccessId != null) {
        onBookingSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Booking Summary", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SoftGold)
        Text("Review appointment details before confirming", fontSize = 12.sp, color = TextMuted)

        Spacer(modifier = Modifier.height(16.dp))

        // Appointment Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("APPOINTMENT DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftGold)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Service:", fontSize = 13.sp, color = TextMuted)
                    Text(service.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextLight)
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Duration:", fontSize = 13.sp, color = TextMuted)
                    Text("${service.durationMin} minutes", fontSize = 13.sp, color = TextLight)
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Date & Time:", fontSize = 13.sp, color = TextMuted)
                    Text("${uiState.selectedDate} at ${slot.displayTime}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WarmGold)
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Stylist:", fontSize = 13.sp, color = TextMuted)
                    Text(uiState.selectedStaff?.name ?: "Anita Sharma", fontSize = 13.sp, color = TextLight)
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Mode:", fontSize = 13.sp, color = TextMuted)
                    Text(if (uiState.bookingMode == "atHome") "🏡 At-Home Service" else "💈 In-Salon", fontSize = 13.sp, color = TextLight)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // At-Home Address Form if applicable
        if (uiState.bookingMode == "atHome") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SERVICE ADDRESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftGold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = addressLine1,
                        onValueChange = {
                            addressLine1 = it
                            viewModel.updateHomeAddress(addressLine1, addressLine2, pincode)
                        },
                        label = { Text("Flat / House No. / Street", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmGold,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = addressLine2,
                        onValueChange = {
                            addressLine2 = it
                            viewModel.updateHomeAddress(addressLine1, addressLine2, pincode)
                        },
                        label = { Text("Landmark / Sector", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmGold,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pincode,
                        onValueChange = {
                            pincode = it
                            viewModel.updateHomeAddress(addressLine1, addressLine2, pincode)
                        },
                        label = { Text("Pincode", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmGold,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Pricing Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("PRICING BREAKDOWN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftGold)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Service Subtotal:", fontSize = 13.sp, color = TextMuted)
                    Text("₹%.2f".format(subtotal / 100.0), fontSize = 13.sp, color = TextLight)
                }

                if (uiState.bookingMode == "atHome") {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Home Visit Charge:", fontSize = 13.sp, color = TextMuted)
                        Text("₹%.2f".format(homeCharge / 100.0), fontSize = 13.sp, color = TextLight)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("GST (${uiState.settings.taxPercent}%):", fontSize = 13.sp, color = TextMuted)
                    Text("₹%.2f".format(tax / 100.0), fontSize = 13.sp, color = TextLight)
                }

                Divider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Payable:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextLight)
                    Text("₹%.2f".format(total / 100.0), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WarmGold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "ℹ️ Note: Your booking request will be reviewed and confirmed by the salon owner.",
            color = SoftGold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = WarmGold)
            }
        } else {
            Button(
                onClick = { viewModel.confirmBooking() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WarmGold)
            ) {
                Text("Send Booking Request", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SlateDark)
            }
        }
    }
}
