package com.github.kanaria32.lottery

import java.time.LocalDateTime

data class Lottery(
    val id: Long,
    val purchasedDate: LocalDateTime,
    val player: String
)
