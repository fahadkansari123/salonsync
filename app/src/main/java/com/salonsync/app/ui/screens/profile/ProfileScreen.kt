package com.salonsync.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salonsync.app.data.model.Role
import com.salonsync.app.data.model.User
import com.salonsync.app.ui.theme.AccentGreen
import com.salonsync.app.ui.theme.AccentRed
import com.salonsync.app.ui.theme.CardBorder
import com.salonsync.app.ui.theme.RichPurple
import com.salonsync.app.ui.theme.SlateDark
import com.salonsync.app.ui.theme.SoftGold
import com.salonsync.app.ui.theme.SurfaceDark
import com.salonsync.app.ui.theme.TextLight
import com.salonsync.app.ui.theme.TextMuted
import com.salonsync.app.ui.theme.WarmGold
import com.salonsync.app.ui.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    user: User,
    authViewModel: AuthViewModel,
    onSignOut: () -> Unit
) {
    var nameInput by remember(user.name) { mutableStateOf(user.name) }
    var phoneInput by remember(user.phone) { mutableStateOf(user.phone) }
    val profileMessage by authViewModel.profileMessage.collectAsState()
    var showSignOutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "My Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SoftGold
        )
        Text(
            text = "Manage your account details and preferences",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar & Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = CardBorder.let { androidx.compose.foundation.BorderStroke(1.dp, it) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(RichPurple)
                        .border(2.dp, WarmGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = user.name.trim().take(1).uppercase().ifEmpty { "U" }
                    Text(
                        text = initial,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user.name.ifEmpty { "Salon User" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )

                Text(
                    text = user.email,
                    fontSize = 13.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Role Badge
                val isOwner = user.roleEnum == Role.ADMIN
                val badgeColor = if (isOwner) WarmGold else SoftGold
                val badgeText = when (user.roleEnum) {
                    Role.ADMIN -> "👑 SALON OWNER / ADMIN"
                    Role.STAFF -> "✂️ SALON STYLIST"
                    else -> "💈 VALUED CUSTOMER"
                }

                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
                        .border(1.dp, badgeColor.copy(alpha = 0.4f), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Editable Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = CardBorder.let { androidx.compose.foundation.BorderStroke(1.dp, it) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PERSONAL INFORMATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftGold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = {
                        nameInput = it
                        authViewModel.clearProfileMessage()
                    },
                    label = { Text("Full Name", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = WarmGold) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarmGold,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = {
                        phoneInput = it
                        authViewModel.clearProfileMessage()
                    },
                    label = { Text("Phone Number", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = WarmGold) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarmGold,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = user.email,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Email Address (Registered)", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CardBorder,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextMuted,
                        unfocusedTextColor = TextMuted
                    )
                )

                if (profileMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val isSuccess = profileMessage?.contains("successfully", ignoreCase = true) == true
                    Text(
                        text = profileMessage!!,
                        color = if (isSuccess) AccentGreen else AccentRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { authViewModel.updateUserProfile(nameInput, phoneInput) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WarmGold)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold, color = SlateDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Salon Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = CardBorder.let { androidx.compose.foundation.BorderStroke(1.dp, it) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Store, contentDescription = null, tint = SoftGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (user.roleEnum == Role.ADMIN) "SALON BUSINESS DETAILS" else "SALON CONTACT & LOCATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Salon Name:", fontSize = 13.sp, color = TextMuted)
                    Text("Glow Salon", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextLight)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Helpline Phone:", fontSize = 13.sp, color = TextMuted)
                    Text("+91 98765 43210", fontSize = 13.sp, color = TextLight)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Address:", fontSize = 13.sp, color = TextMuted)
                    Text("123 Main Street, Sector 5", fontSize = 13.sp, color = TextLight)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Home Service:", fontSize = 13.sp, color = TextMuted)
                    Text("Available (Within 10 km)", fontSize = 13.sp, color = AccentGreen, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security Card (Password Reset)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = CardBorder.let { androidx.compose.foundation.BorderStroke(1.dp, it) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SECURITY & PASSWORD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Need to change your account password? We will send an official password reset link to your registered email.",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { authViewModel.sendPasswordResetForCurrentUser() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftGold),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftGold)
                ) {
                    Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Password Reset Link", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sign Out Button
        Button(
            onClick = { showSignOutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed.copy(alpha = 0.2f))
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = AccentRed, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out", color = AccentRed, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Sign Out Confirmation Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor = SurfaceDark,
            title = {
                Text("Log Out", color = TextLight, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Text("Are you sure you want to log out of SalonSync?", color = TextMuted, fontSize = 14.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        onSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Yes, Log Out", color = TextLight, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}
