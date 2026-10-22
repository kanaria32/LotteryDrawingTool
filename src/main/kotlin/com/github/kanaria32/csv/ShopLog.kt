package com.github.kanaria32.csv

import java.time.LocalDateTime
import java.util.UUID

data class ShopLog(
    val purchasedDate: LocalDateTime,
    val player: String,
    val amount: Int,
    val uuid: UUID = UUID.randomUUID()
)
