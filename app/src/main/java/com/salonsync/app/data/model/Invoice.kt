package com.salonsync.app.data.model

data class InvoiceLineItem(
    val description: String = "",
    val qty: Int = 1,
    val unitPrice: Long = 0L,
    val amount: Long = 0L
)

data class Invoice(
    val id: String = "",
    val invoiceNo: String = "",
    val bookingId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val lineItems: List<InvoiceLineItem> = emptyList(),
    val subtotal: Long = 0L,
    val discount: Long = 0L,
    val taxPercent: Int = 18,
    val tax: Long = 0L,
    val total: Long = 0L,
    val status: String = "paid",
    val issuedAt: Long = System.currentTimeMillis()
) {
    val totalFormatted: String get() = "₹%.2f".format(total / 100.0)
}
