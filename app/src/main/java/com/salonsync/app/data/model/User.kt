package com.salonsync.app.data.model

enum class Role {
    CUSTOMER, STAFF, ADMIN;

    companion object {
        fun fromString(value: String?): Role {
            return when (value?.lowercase()) {
                "admin" -> ADMIN
                "staff" -> STAFF
                else -> CUSTOMER
            }
        }
    }
}

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "customer",
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val active: Boolean = true
) {
    val roleEnum: Role get() = Role.fromString(role)
}
