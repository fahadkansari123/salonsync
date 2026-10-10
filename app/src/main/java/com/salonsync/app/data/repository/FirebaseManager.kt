package com.salonsync.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.salonsync.app.data.model.Booking
import com.salonsync.app.data.model.Invoice
import com.salonsync.app.data.model.Role
import com.salonsync.app.data.model.Service
import com.salonsync.app.data.model.Settings
import com.salonsync.app.data.model.Staff
import com.salonsync.app.data.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object FirebaseManager {

    private const val DATABASE_URL = "https://salonsync-c519b-default-rtdb.firebaseio.com"

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val database: FirebaseDatabase by lazy {
        val instance = FirebaseDatabase.getInstance(DATABASE_URL)
        try {
            instance.setPersistenceEnabled(true)
        } catch (e: Exception) {
            // Persistence already enabled or initialized
        }
        instance
    }

    val currentUser: FirebaseUser? get() = auth.currentUser

    // Observe Current User Profile
    fun observeUserProfile(uid: String): Flow<User?> = callbackFlow {
        val ref = database.getReference("users").child(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                trySend(user)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Save User Profile
    suspend fun saveUserProfile(user: User): Result<Unit> {
        return try {
            database.getReference("users").child(user.uid).setValue(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Observe Services List
    fun observeServices(): Flow<List<Service>> = callbackFlow {
        val ref = database.getReference("services")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Service>()
                for (child in snapshot.children) {
                    val s = child.getValue(Service::class.java)
                    if (s != null && s.active) {
                        list.add(s.copy(id = child.key ?: ""))
                    }
                }
                list.sortBy { it.sortOrder }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Save/Update Service (Admin)
    suspend fun saveService(service: Service): Result<Unit> {
        return try {
            val key = service.id.ifEmpty { database.getReference("services").push().key ?: "svc_${System.currentTimeMillis()}" }
            val item = service.copy(id = key)
            database.getReference("services").child(key).setValue(item).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete Service (Admin)
    suspend fun deleteService(serviceId: String): Result<Unit> {
        return try {
            database.getReference("services").child(serviceId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Observe Staff Members
    fun observeStaff(): Flow<List<Staff>> = callbackFlow {
        val ref = database.getReference("staff")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Staff>()
                for (child in snapshot.children) {
                    val s = child.getValue(Staff::class.java)
                    if (s != null && s.active) {
                        list.add(s.copy(id = child.key ?: ""))
                    }
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Observe Occupied Slots for a given date
    fun observeOccupiedSlots(date: String): Flow<Set<String>> = callbackFlow {
        val ref = database.getReference("slots").child(date)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val occupied = mutableSetOf<String>()
                for (staffNode in snapshot.children) {
                    for (slotNode in staffNode.children) {
                        slotNode.key?.let { occupied.add(it) }
                    }
                }
                trySend(occupied)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Create Booking & Block Slots
    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            val bookingId = database.getReference("bookings").push().key ?: "bk_${System.currentTimeMillis()}"
            val finalBooking = booking.copy(id = bookingId)

            val updates = mutableMapOf<String, Any>()
            updates["/bookings/$bookingId"] = finalBooking
            updates["/userBookings/${booking.customerId}/$bookingId"] = mapOf(
                "date" to booking.date,
                "startTime" to booking.startTime,
                "status" to booking.status,
                "mode" to booking.mode,
                "total" to booking.pricing.total,
                "title" to (booking.services.firstOrNull()?.name ?: "Salon Appointment"),
                "createdAt" to booking.createdAt
            )

            // Block slots
            val staffId = booking.staffId.ifEmpty { "default_staff" }
            for (slotKey in booking.slotKeys) {
                updates["/slots/${booking.date}/$staffId/$slotKey"] = mapOf(
                    "status" to "booked",
                    "bookingId" to bookingId,
                    "customerId" to booking.customerId,
                    "mode" to booking.mode
                )
            }

            database.reference.updateChildren(updates).await()
            Result.success(bookingId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Observe Customer Bookings
    fun observeCustomerBookings(uid: String): Flow<List<Booking>> = callbackFlow {
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Booking>()
                for (child in snapshot.children) {
                    val b = child.getValue(Booking::class.java)
                    if (b != null && b.customerId == uid) {
                        list.add(b)
                    }
                }
                list.sortByDescending { it.createdAt }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Observe All Bookings (Admin)
    fun observeAllBookings(): Flow<List<Booking>> = callbackFlow {
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Booking>()
                for (child in snapshot.children) {
                    val b = child.getValue(Booking::class.java)
                    if (b != null) list.add(b)
                }
                list.sortByDescending { it.createdAt }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Update Booking Status
    suspend fun updateBookingStatus(bookingId: String, newStatus: String): Result<Unit> {
        return try {
            val snap = database.getReference("bookings").child(bookingId).get().await()
            val b = snap.getValue(Booking::class.java)

            val updates = mutableMapOf<String, Any>()
            updates["/bookings/$bookingId/status"] = newStatus

            if (b != null) {
                // Keep customer-side index in sync
                updates["/userBookings/${b.customerId}/$bookingId/status"] = newStatus

                // If cancelled, free up the reserved slots in real-time
                if (newStatus == "cancelled") {
                    val staffId = b.staffId.ifEmpty { "default_staff" }
                    for (slotKey in b.slotKeys) {
                        try {
                            database.getReference("slots")
                                .child(b.date)
                                .child(staffId)
                                .child(slotKey)
                                .removeValue()
                                .await()
                        } catch (ignored: Exception) {}
                    }
                }

                // If completed, generate invoice
                if (newStatus == "completed") {
                    val invoiceId = "inv_${System.currentTimeMillis()}"
                    val invoice = Invoice(
                        id = invoiceId,
                        invoiceNo = "INV-${System.currentTimeMillis().toString().takeLast(6)}",
                        bookingId = bookingId,
                        customerId = b.customerId,
                        customerName = b.customerName,
                        subtotal = b.pricing.subtotal,
                        tax = b.pricing.tax,
                        total = b.pricing.total,
                        status = "paid"
                    )
                    updates["/billing/$invoiceId"] = invoice
                    updates["/userBilling/${b.customerId}/$invoiceId"] = invoice
                }
            }

            database.reference.updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Observe Settings
    fun observeSettings(): Flow<Settings> = callbackFlow {
        val ref = database.getReference("settings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val s = snapshot.getValue(Settings::class.java) ?: Settings()
                trySend(s)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
