package com.salonsync.app.data.model

data class SalonInfo(
    val name: String = "Glow Salon",
    val phone: String = "+919876543210",
    val address: String = "123 Main Street, Sector 5",
    val lat: Double = 19.2813,
    val lng: Double = 73.0483
)

data class HomeServiceConfig(
    val enabled: Boolean = true,
    val radiusKm: Int = 10,
    val charge: Long = 10000L, // Rs 100
    val travelBufferMin: Int = 30
)

data class Settings(
    val salon: SalonInfo = SalonInfo(),
    val slotIntervalMin: Int = 30,
    val advanceBookingDays: Int = 30,
    val cancellationWindowHours: Int = 4,
    val autoConfirm: Boolean = true,
    val homeService: HomeServiceConfig = HomeServiceConfig(),
    val taxPercent: Int = 18,
    val currency: String = "INR"
)
