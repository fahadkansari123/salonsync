package com.salonsync.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salonsync.app.data.model.Booking
import com.salonsync.app.data.repository.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class StaffUiState(
    val todayBookings: List<Booking> = emptyList(),
    val selectedDate: String = LocalDate.now().toString(),
    val isLoading: Boolean = false
)

class StaffViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StaffUiState())
    val uiState: StateFlow<StaffUiState> = _uiState.asStateFlow()

    init {
        loadStaffBookings()
    }

    fun loadStaffBookings() {
        viewModelScope.launch {
            FirebaseManager.observeAllBookings().collect { allBookings ->
                val filtered = allBookings.filter { b -> b.date == _uiState.value.selectedDate }
                _uiState.value = _uiState.value.copy(todayBookings = filtered)
            }
        }
    }

    fun updateStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            FirebaseManager.updateBookingStatus(bookingId, status)
        }
    }
}
