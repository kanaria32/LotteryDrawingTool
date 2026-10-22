package com.github.kanaria32.lottery

import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.random.Random

class LotteryDrawer(
    private val lotteries: List<Lottery>,
    private val seed: Long
) {

    private companion object {
        val SEPARATOR = "=".repeat(150)
        val SECTION_LINE = "―".repeat(10)
        val YEN_FORMAT = DecimalFormat("¥#,###")

        // 前期繰越分と取り崩し率
        val BALANCE_BROUGHT_FORWARD = BigDecimal(1913100)
        val WITHDRAWAL_RATE = BigDecimal("0.25")

        // チケットの枚単価と手数料
        val TICKET_PRICE = BigDecimal(1000)
        val COMMISSION_RATE = BigDecimal("0.15")

        // 1～3等の排出本数
        val FIRST_PRIZE_COUNT = BigDecimal(1)
        val SECOND_PRIZE_COUNT = BigDecimal(3)
        val THIRD_PRIZE_COUNT = BigDecimal(10)

        // 4等, 5等の払い戻し金額
        val FOURTH_REFUND_AMOUNT = BigDecimal(2000)
        val FIFTH_REFUND_AMOUNT = BigDecimal(1000)
    }

    // プール額の計算
    private val totalSales = BigDecimal(lotteries.size) * TICKET_PRICE
    private val commission = totalSales * COMMISSION_RATE
    private val withdrawal = BALANCE_BROUGHT_FORWARD * WITHDRAWAL_RATE

    // プール額 = 販売額 - 手数料 + 前期繰越からの取り崩し分
    private val prizePool = totalSales - commission + withdrawal

    private val prizeCounts: Map<PrizeType, BigDecimal> = mapOf(
        PrizeType.FIRST to FIRST_PRIZE_COUNT,
        PrizeType.SECOND to SECOND_PRIZE_COUNT,
        PrizeType.THIRD to THIRD_PRIZE_COUNT,
        PrizeType.FOURTH to (prizePool * PrizeType.FOURTH.rate / FOURTH_REFUND_AMOUNT),
        PrizeType.FIFTH to (prizePool * PrizeType.FIFTH.rate / FIFTH_REFUND_AMOUNT)
    )

    private val refundAmounts: Map<PrizeType, BigDecimal> = mapOf(
        PrizeType.FIRST to (prizePool * PrizeType.FIRST.rate / prizeCounts.getValue(PrizeType.FIRST)),
        PrizeType.SECOND to (prizePool * PrizeType.SECOND.rate / prizeCounts.getValue(PrizeType.SECOND)),
        PrizeType.THIRD to (prizePool * PrizeType.THIRD.rate / prizeCounts.getValue(PrizeType.THIRD)),
        PrizeType.FOURTH to FOURTH_REFUND_AMOUNT,
        PrizeType.FIFTH to FIFTH_REFUND_AMOUNT
    )

    private val winners: List<Winner> = draw()

    private fun draw(): List<Winner> {
        val random = Random(seed)
        val tickets = lotteries.shuffled(random).iterator()
        val winners = mutableListOf<Winner>()

        PrizeType.entries.forEach { type ->
            repeat(prizeCounts.getValue(type).toInt()) {
                winners.add(
                    Winner(tickets.next().player, type)
                )
            }
        }

        return winners
    }

    fun showResult() {
        val result = buildString {
            appendLine(SEPARATOR)
            appendSalesSection()
            appendRefundSection()
            appendWinnersSection()
            appendLine("※4等以下の場合は、複数当選していてもプレイヤー名は1つしか表示されません。")
            appendLine(SEPARATOR)
            appendPlayerSummarySection()
        }

        println(result)
    }

    private fun StringBuilder.appendSalesSection() {
        appendLine(SECTION_LINE)
        appendLine("A 販売額: ${totalSales.toYen()}")
        appendLine("B 前期繰越からの取り崩し（${WITHDRAWAL_RATE * 100.toBigDecimal()}％）: ${withdrawal.toLong().toYen()}")
        appendLine("C 手数料（${COMMISSION_RATE * 100.toBigDecimal()}％）: ${commission.toLong().toYen()}")
        appendLine(SECTION_LINE)
        appendLine("プール額（A + B - C）: ${prizePool.toLong().toYen()}")
        appendLine()
    }

    private fun StringBuilder.appendRefundSection() {
        appendLine("払い戻し金")
        PrizeType.entries.forEach { type ->
            appendLine("${type.label} ${refundAmounts.getValue(type).toYen()}")
        }
        appendLine()
    }

    private fun StringBuilder.appendWinnersSection() {
        appendLine("当選者")

        val wins = winners.groupBy({ it.prizeType }) { it.player }
                .mapValues { (_, players) -> players.sorted() }

        PrizeType.entries.forEach { type ->
            val w = wins[type].orEmpty()
            val displayedWinners = when (type) {
                PrizeType.FOURTH,
                PrizeType.FIFTH -> w.distinct()
                else -> w
            }

            appendLine("${type.label} (${w.size}本)")
            displayedWinners.chunked(10).forEach { appendLine(it.joinToString(", ")) }
            appendLine()
        }
    }

    private fun StringBuilder.appendPlayerSummarySection() {
        val winCountsByPlayer = winners.groupBy { it.player }
            .mapValues { (_, playerWins) ->
                playerWins.groupingBy { it.prizeType }.eachCount()
            }

        winCountsByPlayer.forEach { (player, countsByPrize) ->
            val summary = countsByPrize.entries.joinToString(", ") { (type, count) ->
                "(${type.label}: $count)"
            }

            var totalRefund = BigDecimal(0)
            countsByPrize.entries.forEach { (type, count) ->
                totalRefund += refundAmounts.getValue(type) * BigDecimal(count)
            }

            appendLine("[$player] $summary -> ${totalRefund.toYen()}")
        }
    }

    private fun BigDecimal.toYen(): String = YEN_FORMAT.format(this)

    private fun Long.toYen(): String = YEN_FORMAT.format(this)
}
