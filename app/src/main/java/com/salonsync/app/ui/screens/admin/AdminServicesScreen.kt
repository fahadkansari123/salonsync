package com.salonsync.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.salonsync.app.data.model.Service
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
fun AdminServicesScreen(
    viewModel: AdminViewModel,
    uiState: AdminUiState
) {
    var editingServiceId by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Hair") }
    var desc by remember { mutableStateOf("") }
    var durationMin by remember { mutableStateOf("45") }
    var priceRupees by remember { mutableStateOf("500") }
    var inSalon by remember { mutableStateOf(true) }
    var atHome by remember { mutableStateOf(true) }

    var serviceToDelete by remember { mutableStateOf<Service?>(null) }

    fun clearForm() {
        editingServiceId = null
        name = ""
        category = "Hair"
        desc = ""
        durationMin = "45"
        priceRupees = "500"
        inSalon = true
        atHome = true
    }

    fun populateFormForEdit(service: Service) {
        editingServiceId = service.id
        name = service.name
        category = service.category
        desc = service.description
        durationMin = service.durationMin.toString()
        priceRupees = (service.price / 100.0).toString()
        inSalon = service.modes.inSalon
        atHome = service.modes.atHome
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(16.dp)
    ) {
        Text("Service Management", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SoftGold)
        Text("Add, edit, or delete services offered by Glow Salon", fontSize = 12.sp, color = TextMuted)

        Spacer(modifier = Modifier.height(14.dp))

        // Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (editingServiceId != null) "EDIT SERVICE" else "ADD NEW SERVICE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarmGold
                    )

                    if (editingServiceId != null) {
                        TextButton(onClick = { clearForm() }) {
                            Text("Cancel Edit", color = AccentRed, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Service Name", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WarmGold, unfocusedBorderColor = CardBorder, focusedTextColor = TextLight, unfocusedTextColor = TextLight)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category", color = TextMuted) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WarmGold, unfocusedBorderColor = CardBorder, focusedTextColor = TextLight, unfocusedTextColor = TextLight)
                    )

                    OutlinedTextField(
                        value = priceRupees,
                        onValueChange = { priceRupees = it },
                        label = { Text("Price (₹)", color = TextMuted) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WarmGold, unfocusedBorderColor = CardBorder, focusedTextColor = TextLight, unfocusedTextColor = TextLight)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = durationMin,
                    onValueChange = { durationMin = it },
                    label = { Text("Duration (Mins)", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WarmGold, unfocusedBorderColor = CardBorder, focusedTextColor = TextLight, unfocusedTextColor = TextLight)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = inSalon, onCheckedChange = { inSalon = it }, colors = CheckboxDefaults.colors(checkedColor = WarmGold))
                    Text("In-Salon", fontSize = 12.sp, color = TextLight)

                    Checkbox(checked = atHome, onCheckedChange = { atHome = it }, colors = CheckboxDefaults.colors(checkedColor = WarmGold), modifier = Modifier.padding(start = 12.dp))
                    Text("At-Home", fontSize = 12.sp, color = TextLight)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addOrUpdateService(
                                serviceId = editingServiceId ?: "",
                                name = name,
                                cat = category,
                                desc = desc,
                                durationMin = durationMin.toIntOrNull() ?: 30,
                                priceRupees = priceRupees.toDoubleOrNull() ?: 500.0,
                                inSalon = inSalon,
                                atHome = atHome
                            )
                            clearForm()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WarmGold)
                ) {
                    Text(
                        text = if (editingServiceId != null) "Update Service" else "Save Service",
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("Active Services (${uiState.services.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextLight)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.services) { service ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(service.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextLight)
                            Text("${service.category} • ${service.durationMin} mins", fontSize = 11.sp, color = TextMuted)
                            Text(service.priceFormatted, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WarmGold)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { populateFormForEdit(service) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Service", tint = SoftGold)
                            }

                            IconButton(onClick = { serviceToDelete = service }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Service", tint = AccentRed)
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (serviceToDelete != null) {
        val s = serviceToDelete!!
        AlertDialog(
            onDismissRequest = { serviceToDelete = null },
            containerColor = SurfaceDark,
            title = { Text("Delete Service", color = AccentRed, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = { Text("Are you sure you want to delete '${s.name}'? This action cannot be undone.", color = TextLight, fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteService(s.id)
                        serviceToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Delete", color = TextLight, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { serviceToDelete = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}
