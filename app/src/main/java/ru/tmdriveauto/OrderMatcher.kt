package ru.tmdriveauto

data class OrderCandidate(
    val price: Double,
    val pickupAddress: String,
    val destinationAddress: String
)

data class OrderFilter(
    val minimumPrice: Double,
    val keywords: List<String>
)

object OrderMatcher {
    fun matches(order: OrderCandidate, filter: OrderFilter): Boolean {
        if (order.price < filter.minimumPrice) return false
        val terms = filter.keywords.map { it.trim() }.filter { it.isNotEmpty() }
        if (terms.isEmpty()) return true
        val searchable = "${order.pickupAddress} ${order.destinationAddress}".lowercase()
        return terms.any { searchable.contains(it.lowercase()) }
    }
}
