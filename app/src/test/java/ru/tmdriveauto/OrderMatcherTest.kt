package ru.tmdriveauto

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OrderMatcherTest {
    private val filter = OrderFilter(300.0, listOf("аэропорт", "вокзал"))

    @Test fun rejectsPriceBelowMinimum() {
        assertFalse(OrderMatcher.matches(OrderCandidate(299.0, "Центр", "Вокзал"), filter))
    }

    @Test fun matchesKeywordInPickupAddress() {
        assertTrue(OrderMatcher.matches(OrderCandidate(350.0, "Аэропорт", "Центр"), filter))
    }

    @Test fun matchesKeywordInDestinationAddress() {
        assertTrue(OrderMatcher.matches(OrderCandidate(350.0, "Центр", "Главный Вокзал"), filter))
    }

    @Test fun acceptsWhenAnyKeywordMatches() {
        assertTrue(OrderMatcher.matches(OrderCandidate(300.0, "Центр", "Вокзал"), filter))
    }

    @Test fun emptyKeywordsAllowAnyAddressAbovePrice() {
        assertTrue(OrderMatcher.matches(OrderCandidate(300.0, "Улица 1", "Улица 2"), OrderFilter(300.0, emptyList())))
    }
}
