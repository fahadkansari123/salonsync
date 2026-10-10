package com.salonsync.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salonsync.app.data.model.Booking
import com.salonsync.app.data.model.Service
import com.salonsync.app.data.model.Staff
import com.salonsync.app.data.repository.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val bookings: List<Booking> = emptyList(),
    val services: List<Service> = emptyList(),
    val staffList: List<Staff> = emptyList(),
    val selectedDate: String = java.time.LocalDate.now().toString(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadAllBookings()
        loadServices()
        loadStaff()
    }

    fun loadAllBookings() {
        viewModelScope.launch {
            FirebaseManager.observeAllBookings().collect { bookings ->
                _uiState.value = _uiState.value.copy(bookings = bookings)
            }
        }
    }

    fun loadServices() {
        viewModelScope.launch {
            FirebaseManager.observeServices().collect { services ->
                _uiState.value = _uiState.value.copy(services = services)
            }
        }
    }

    fun loadStaff() {
        viewModelScope.launch {
            FirebaseManager.observeStaff().collect { staff ->
                _uiState.value = _uiState.value.copy(staffList = staff)
            }
        }
    }

    fun updateBookingStatus(bookingId: String, newStatus: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val res = FirebaseManager.updateBookingStatus(bookingId, newStatus)
            if (res.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = res.exceptionOrNull()?.localizedMessage
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun addOrUpdateService(
        serviceId: String = "",
        name: String,
        cat: String,
        desc: String,
        durationMin: Int,
        priceRupees: Double,
        inSalon: Boolean,
        atHome: Boolean
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val service = Service(
                id = serviceId,
                name = name,
                category = cat,
                description = desc,
                durationMin = durationMin,
                price = (priceRupees * 100).toLong(),
                modes = com.salonsync.app.data.model.ServiceModes(inSalon, atHome)
            )
            FirebaseManager.saveService(service)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            FirebaseManager.deleteService(serviceId)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}
