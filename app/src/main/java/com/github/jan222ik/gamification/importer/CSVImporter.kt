package com.github.jan222ik.gamification.importer

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.jan222ik.gamification.data.CardData


object CSVImporter {

    private const val id = "id"
    private const val description = "description"
    private const val effectDelay = "effectDelay"
    private const val effectForReceiver = "effectForReceiver"
    private const val effectForSender = "effectForSender"
    private const val activateOnCards = "activateOnCards"
    private const val hasReceiver = "hasReceiver"

    private val csv = """
        |id,activateOnCards,effectDelay,effectForReceiver,effectForSender,description,hasReceiver
        |1,1,0,-1,0,TEST: Minus 1 Instant,FALSE
        |2,2,0,-2,0,TEST: Minus 2 Instant,TRUE
        |3,3,0,-1,0,TEST: Minus 1 Instant,FALSE
        |4,3,0,-1,0,TEST: Minus 1 Instant,FALSE
    """.trimMargin()

    fun loadCards(csvString: String = csv) : List<CardData> {
        return csvReader().readAllWithHeader(csvString).map { row: Map<String, String> ->
            //println(row)
            CardData(
                id = row[id]!!.toInt(),
                description = row[description]!!,
                effectDelay = row[effectDelay]!!.toInt(),
                effectForReceiver = row[effectForReceiver]!!.toInt(),
                effectForSender= row[effectForSender]!!.toInt(),
                activateOnCards = row[activateOnCards]!!.split(",").map { it.toInt() },
                hasReceiver = row[hasReceiver].toBoolean()
            )//.also(::println)
        }
    }


}