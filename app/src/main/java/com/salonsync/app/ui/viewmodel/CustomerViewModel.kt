package com.salonsync.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salonsync.app.data.model.AddressSnapshot
import com.salonsync.app.data.model.BookedServiceItem
import com.salonsync.app.data.model.Booking
import com.salonsync.app.data.model.Pricing
import com.salonsync.app.data.model.Service
import com.salonsync.app.data.model.Settings
import com.salonsync.app.data.model.SlotState
import com.salonsync.app.data.model.Staff
import com.salonsync.app.data.repository.FirebaseManager
import com.salonsync.app.domain.AvailabilityEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CustomerUiState(
    val services: List<Service> = emptyList(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val selectedService: Service? = null,
    val bookingMode: String = "inSalon", // inSalon | atHome
    val selectedDate: String = LocalDate.now().toString(),
    val occupiedSlots: Set<String> = emptySet(),
    val slots: List<SlotState> = emptyList(),
    val selectedSlot: SlotState? = null,
    val staffList: List<Staff> = emptyList(),
    val selectedStaff: Staff? = null,
    val myBookings: List<Booking> = emptyList(),
    val settings: Settings = Settings(),
    val homeAddress: AddressSnapshot = AddressSnapshot("Flat 12, Green Residency", "Sector 5", "421302"),
    val currentUserProfile: com.salonsync.app.data.model.User? = null,
    val isLoading: Boolean = false,
    val bookingSuccessId: String? = null,
    val errorMessage: String? = null
)

class CustomerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerUiState())
    val uiState: StateFlow<CustomerUiState> = _uiState.asStateFlow()

    init {
        loadServices()
        loadStaff()
        loadSettings()
        observeOccupiedSlots(_uiState.value.selectedDate)
        loadMyBookings()
        loadUserProfile()
    }

    fun loadUserProfile() {
        val uid = FirebaseManager.currentUser?.uid ?: return
        viewModelScope.launch {
            FirebaseManager.observeUserProfile(uid).collect { user ->
                _uiState.value = _uiState.value.copy(currentUserProfile = user)
            }
        }
    }

    private fun loadServices() {
        viewModelScope.launch {
            FirebaseManager.observeServices().collect { services ->
                _uiState.value = _uiState.value.copy(services = services)
            }
        }
    }

    private fun loadStaff() {
        viewModelScope.launch {
            FirebaseManager.observeStaff().collect { staff ->
                _uiState.value = _uiState.value.copy(staffList = staff)
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            FirebaseManager.observeSettings().collect { settings ->
                _uiState.value = _uiState.value.copy(settings = settings)
            }
        }
    }

    fun observeOccupiedSlots(date: String) {
        viewModelScope.launch {
            FirebaseManager.observeOccupiedSlots(date).collect { occupied ->
                val serviceDuration = _uiState.value.selectedService?.durationMin ?: 30
                val reqSlots = (serviceDuration + 29) / 30
                val generatedSlots = AvailabilityEngine.generateSlots(
                    selectedDate = date,
                    intervalMin = 30,
                    requiredSlots = reqSlots,
                    occupiedSlotKeys = occupied
                )
                _uiState.value = _uiState.value.copy(
                    selectedDate = date,
                    occupiedSlots = occupied,
                    slots = generatedSlots
                )
            }
        }
    }

    fun loadMyBookings() {
        val uid = FirebaseManager.currentUser?.uid ?: return
        viewModelScope.launch {
            FirebaseManager.observeCustomerBookings(uid).collect { bookings ->
                _uiState.value = _uiState.value.copy(myBookings = bookings)
            }
        }
    }

    fun selectCategory(cat: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = cat)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun selectService(service: Service) {
        _uiState.value = _uiState.value.copy(selectedService = service, selectedSlot = null)
        observeOccupiedSlots(_uiState.value.selectedDate)
    }

    fun selectMode(mode: String) {
        _uiState.value = _uiState.value.copy(bookingMode = mode)
    }

    fun selectDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedSlot = null)
        observeOccupiedSlots(date)
    }

    fun selectSlot(slot: SlotState) {
        _uiState.value = _uiState.value.copy(selectedSlot = slot)
    }

    fun selectStaff(staff: Staff?) {
        _uiState.value = _uiState.value.copy(selectedStaff = staff)
    }

    fun updateHomeAddress(line1: String, line2: String, pincode: String) {
        _uiState.value = _uiState.value.copy(
            homeAddress = AddressSnapshot(line1, line2, pincode)
        )
    }

    fun confirmBooking() {
        val state = _uiState.value
        val service = state.selectedService ?: return
        val slot = state.selectedSlot ?: return
        val user = FirebaseManager.currentUser ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val subtotal = service.price
            val homeCharge = if (state.bookingMode == "atHome") state.settings.homeService.charge else 0L
            val tax = ((subtotal + homeCharge) * state.settings.taxPercent) / 100
            val total = subtotal + homeCharge + tax

            val reqSlots = (service.durationMin + 29) / 30
            val slotKeys = mutableListOf<String>()
            val hour = slot.timeKey.take(2).toInt()
            val min = slot.timeKey.takeLast(2).toInt()
            var timeCursor = java.time.LocalTime.of(hour, min)

            for (i in 0 until reqSlots) {
                slotKeys.add("%02d%02d".format(timeCursor.hour, timeCursor.minute))
                timeCursor = timeCursor.plusMinutes(30)
            }

            val endTimeStr = "%02d%02d".format(timeCursor.hour, timeCursor.minute)

            val profile = state.currentUserProfile
            val customerName = profile?.name?.ifBlank { null }
                ?: user.displayName?.ifBlank { null }
                ?: user.email?.substringBefore("@")
                ?: "Customer"
            val customerPhone = profile?.phone?.ifBlank { null }
                ?: user.phoneNumber
                ?: "+919876543210"

            val booking = Booking(
                customerId = user.uid,
                customerName = customerName,
                customerPhone = customerPhone,
                staffId = state.selectedStaff?.id ?: "staff_anita",
                staffName = state.selectedStaff?.name ?: "Anita Sharma",
                services = listOf(
                    BookedServiceItem(service.id, service.name, service.durationMin, service.price)
                ),
                mode = state.bookingMode,
                addressSnapshot = if (state.bookingMode == "atHome") state.homeAddress else null,
                date = state.selectedDate,
                startTime = slot.timeKey,
                endTime = endTimeStr,
                slotKeys = slotKeys,
                pricing = Pricing(subtotal, homeCharge, 0L, tax, total),
                status = "pending",
                paymentStatus = "unpaid"
            )

            val res = FirebaseManager.createBooking(booking)
            if (res.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    bookingSuccessId = res.getOrNull(),
                    selectedService = null,
                    selectedSlot = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = res.exceptionOrNull()?.localizedMessage ?: "Failed to book slot"
                )
            }
        }
    }

    fun clearBookingSuccess() {
        _uiState.value = _uiState.value.copy(bookingSuccessId = null)
    }
}
