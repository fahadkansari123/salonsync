package com.salonsync.app.data.model

enum class SlotStatus {
    AVAILABLE, BOOKED, BLOCKED, PAST
}

data class SlotState(
    val timeKey: String, // e.g. "1000", "1030"
    val displayTime: String, // e.g. "10:00 AM"
    val status: SlotStatus = SlotStatus.AVAILABLE
)
