package com.salonsync.app.data.model

enum class BookingStatus {
    PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW;

    companion object {
        fun fromString(valStr: String?): BookingStatus {
            return try {
                valueOf(valStr?.uppercase() ?: "PENDING")
            } catch (e: Exception) {
                PENDING
            }
        }
    }
}

data class BookedServiceItem(
    val serviceId: String = "",
    val name: String = "",
    val durationMin: Int = 30,
    val price: Long = 0L
)

data class AddressSnapshot(
    val line1: String = "",
    val line2: String = "",
    val pincode: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

data class Pricing(
    val subtotal: Long = 0L,
    val homeCharge: Long = 0L,
    val discount: Long = 0L,
    val tax: Long = 0L,
    val total: Long = 0L
) {
    val totalFormatted: String get() = "₹%.2f".format(total / 100.0)
}

data class Booking(
    val id: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val staffId: String = "",
    val staffName: String = "",
    val services: List<BookedServiceItem> = emptyList(),
    val mode: String = "inSalon", // inSalon | atHome
    val addressSnapshot: AddressSnapshot? = null,
    val date: String = "", // yyyy-MM-dd
    val startTime: String = "", // HHmm e.g. "1430"
    val endTime: String = "",
    val slotKeys: List<String> = emptyList(),
    val pricing: Pricing = Pricing(),
    val status: String = "confirmed",
    val paymentStatus: String = "unpaid",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val statusEnum: BookingStatus get() = BookingStatus.fromString(status)
}
