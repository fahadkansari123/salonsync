package com.salonsync.app.data.model

data class TimeSlotRange(
    val start: String = "10:00",
    val end: String = "19:00"
)

data class Staff(
    val id: String = "",
    val uid: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val skills: Map<String, Boolean> = emptyMap(),
    val offersHomeService: Boolean = true,
    val workingHours: Map<String, TimeSlotRange?> = emptyMap(),
    val active: Boolean = true
)
