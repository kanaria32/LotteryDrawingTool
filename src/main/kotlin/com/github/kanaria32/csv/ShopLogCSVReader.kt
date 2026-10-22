package com.github.kanaria32.csv

import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvToBeanBuilder
import java.io.FileReader
import java.sql.Timestamp

class ShopLogCSVReader(
    private val fileName: String
) {

    fun read(): List<ShopLog> = CsvToBeanBuilder<Beans>(FileReader(fileName))
        .withType(Beans::class.java)
        .build()
        .parse()
        .map(Beans::toShopLog)

    data class Beans(
        @CsvBindByName(column = "timestamp")
        var timestamp: String = "",

        @CsvBindByName(column = "player_name")
        var player: String = "",

        @CsvBindByName(column = "amount")
        var amount: String = ""
    ) {

        fun toShopLog(): ShopLog = ShopLog(
            purchasedDate = Timestamp.valueOf(timestamp).toLocalDateTime(),
            player = player,
            amount = amount.toInt()
        )
    }

}
