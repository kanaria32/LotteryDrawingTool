package com.github.kanaria32.lottery

import java.math.BigDecimal

enum class PrizeType(
    val label: String,
    val rate: BigDecimal
) {
    FIRST("1等", BigDecimal("0.35")),
    SECOND("2等", BigDecimal("0.30")),
    THIRD("3等", BigDecimal("0.20")),
    FOURTH("4等", BigDecimal("0.10")),
    FIFTH("5等", BigDecimal("0.05"))
}
