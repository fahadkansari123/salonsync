package com.salonsync.app.data.model

data class ServiceModes(
    val inSalon: Boolean = true,
    val atHome: Boolean = false
)

data class Service(
    val id: String = "",
    val name: String = "",
    val category: String = "General",
    val description: String = "",
    val durationMin: Int = 30,
    val price: Long = 0L, // minor units e.g. paise (45000 = Rs 450)
    val modes: ServiceModes = ServiceModes(),
    val imageUrl: String = "",
    val active: Boolean = true,
    val sortOrder: Int = 0
) {
    val priceFormatted: String
        get() {
            val mainUnit = price / 100.0
            return "₹%.2f".format(mainUnit)
        }
}
