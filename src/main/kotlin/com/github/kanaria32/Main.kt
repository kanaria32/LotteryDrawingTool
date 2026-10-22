package com.github.kanaria32

import com.github.kanaria32.csv.ShopLogCSVReader
import com.github.kanaria32.lottery.Lottery
import com.github.kanaria32.lottery.LotteryDrawer
import java.nio.ByteBuffer

fun main() {
    val reader = ShopLogCSVReader("economy_logs/2026_autumn/lottery_shopchest_economy_logs.csv")
    val lotteries = reader.read()
        .flatMap { log ->
            List(
                maxOf(log.amount, 1),
                { log }
            )
        }.mapIndexed { index, log ->
            Lottery(
                id = index + 1L,
                purchasedDate = log.purchasedDate,
                player = log.player
            )
        }

    val hash = "b45e991e1ad64501f877c147dfb715da1a0e6ef78e45a550b2983d2e6e78158b"
    val seed = seedFromHash(hash)
    println("SHA256 $hash")
    println("Seed $seed")

    LotteryDrawer(lotteries, seed).showResult()
}

fun seedFromHash(hash: String): Long {
    val bytes = hash.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    val buf = ByteBuffer.wrap(bytes)

    return buf.long xor buf.long xor buf.long xor buf.long
}
