package com.salonsync.app.domain

import com.salonsync.app.data.model.SlotState
import com.salonsync.app.data.model.SlotStatus
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object AvailabilityEngine {

    fun generateSlots(
        selectedDate: String, // "yyyy-MM-dd"
        startWork: String = "10:00",
        endWork: String = "19:00",
        intervalMin: Int = 30,
        requiredSlots: Int = 1,
        occupiedSlotKeys: Set<String> = emptySet()
    ): List<SlotState> {
        val slots = mutableListOf<SlotState>()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        val displayFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

        val startTime = try { LocalTime.parse(startWork, timeFormatter) } catch (e: Exception) { LocalTime.of(10, 0) }
        val endTime = try { LocalTime.parse(endWork, timeFormatter) } catch (e: Exception) { LocalTime.of(19, 0) }

        val todayStr = LocalDate.now().toString()
        val nowTime = LocalTime.now()

        var current = startTime
        while (current.plusMinutes(intervalMin.toLong() * requiredSlots).isBefore(endTime) ||
            current.plusMinutes(intervalMin.toLong() * requiredSlots) == endTime) {

            val slotKey = "%02d%02d".format(current.hour, current.minute)
            val displayStr = current.format(displayFormatter)

            // Check if any of the required consecutive slots are occupied
            var isBlocked = false
            for (i in 0 until requiredSlots) {
                val checkTime = current.plusMinutes(i.toLong() * intervalMin)
                val checkKey = "%02d%02d".format(checkTime.hour, checkTime.minute)
                if (occupiedSlotKeys.contains(checkKey)) {
                    isBlocked = true
                    break
                }
            }

            // Check if slot is in the past for today
            val isPast = selectedDate == todayStr && current.isBefore(nowTime)

            val status = when {
                isPast -> SlotStatus.PAST
                isBlocked -> SlotStatus.BOOKED
                else -> SlotStatus.AVAILABLE
            }

            slots.add(SlotState(timeKey = slotKey, displayTime = displayStr, status = status))

            current = current.plusMinutes(intervalMin.toLong())
        }

        return slots
    }
}
