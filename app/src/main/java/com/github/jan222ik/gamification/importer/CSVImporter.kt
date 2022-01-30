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

    fun loadCards(csvString: String = cards) : List<CardData> {
        return csvReader().readAllWithHeader(csvString).map { row: Map<String, String> ->
            //println(row)
            CardData(
                id = row[id]!!.toInt(),
                description = row[description]!!,
                effectDelay = row[effectDelay]!!.toInt(),
                effectForReceiver = row[effectForReceiver]!!.toInt(),
                effectForSender= row[effectForSender]!!.toInt(),
                activateOnCards = row[activateOnCards]!!.replace(" ", "").split(",").map { it.toInt() },
                hasReceiver = row[hasReceiver].toBoolean()
            )//.also(::println)
        }
    }

    val cards = """
        |id,activateOnCards,effectDelay,effectForReceiver,effectForSender,description,hasReceiver
        |1,"3, 4",0,0,5,Will activate if card 3 and 4 was played,FALSE
        |2,"6, 3",0,0,4,Will activate if card 3 and 6 was played,FALSE
        |3,3,0,-3,1,Will activate if card itself is played,TRUE
        |4,4,0,2,1,Will activate if card itself is played,FALSE
        |5,5,0,0,1,Will activate if card itself is played,TRUE
        |6,6,0,0,1,Will activate if card itself is played,FALSE
        |7,7,0,0,1,Ich poste ein Selfie von mir. ,FALSE
        |8,8,0,0,1,Ich poste ein Selfie von mir. ,FALSE
        |9,9,0,0,1,Ich poste ein Bild von mir und meinem Haustier. ,FALSE
        |10,10,0,0,1,Ich poste ein Bild von mir und meinen Freunden. ,FALSE
        |11,11,0,0,1,Ich poste ein Bild von mir in meinem Urlaub. ,FALSE
        |12,12,0,0,1,Ich poste ein Bild von mir mit meinem neuen Partner/meiner neuen Partnerin. ,FALSE
        |13,13,0,2,1,Ich kommentiere das Bild eines Freundes/einer Freundin. ,TRUE
        |14,14,0,0,1,Ich like das Bild eines Freundes/einer Freundin.,TRUE
        |15,15,0,0,2,"Ich möchte ehrlich mit meinen Followern sein und meine dunkle Vergangenheit mit ihnen teilen, indem ich mich per Live-Video dazu äußere. ",FALSE
        |16,16,0,0,0,Social Media Detox! Ich möchte mir eine digitale Auszeit auf unbestimmte Zeit nehmen und informiere meine Follower darüber. ,FALSE
        |17,17,0,0,2,Ich wurde auf einem Bild mit einem bekannten Influencer/einer bekannten Influencerin markiert. ,FALSE
        |18,18,0,0,-2,Mein Profil wurde gehackt. Eigenartige Nachrichten mit Links werden an meine Follower gesendet. ,FALSE
        |19,19,0,0,2,Ich gehe eine Kooperation mit einer Sportmarke ein. ,FALSE
        |20,20,0,0,2,Ich gehe eine Kooperation mit einer Beautymarke ein. ,FALSE
        |21,21,0,0,-1,Ich gehe eine Kooperation mit einer Partei ein. ,FALSE
        |22,22,0,0,-1,Ich gehe eine Kooperation mit einer Versicherungsanstalt ein. ,FALSE
        |23,23,0,0,2,Ich gehe eine Kooperation mit einem namhaften Technologiekonzern ein. ,FALSE
        |24,24,0,0,2,Ich gehe eine Kooperation mit einer Staubsaugermarke ein. ,FALSE
        |25,25,0,0,0,"Ich vergleiche mich mit anderen Influencern, um zu sehen, ob und wie ich meinen eigenen Auftritt optimieren kann. ",FALSE
        |26,26,0,0,1,Ich gehe vermehrt ins Fitnessstudio und achte auf eine gesunde Ernährung. ,FALSE
        |27,27,0,0,1,"In einem Live Video spreche ich mich zu den aktuellen politischen Vorkomnissen aus. Um authentisch zu bleiben, nehme ich kein Blatt vor den Mund. ",FALSE
        |28,28,0,0,2,"Ich poste ein Bild von mir, wie ich am Strand liege. ",FALSE
        |29,29,0,0,1,Ich beantworte die Nachrichten meiner Follower. ,FALSE
        |30,30,0,0,1,Ich gehe eine Kooperation mit einem anderen Influencer/einer anderen Influencerin ein. ,TRUE
        |31,31,0,0,-1,In einer Story äußere ich mich kritisch zu einem Influencer/einer Influencerin. ,TRUE
        |32,32,0,0,2,Ich gehe eine Kooperation mit einer Fitnessbrand ein. ,FALSE
        |33,33,0,0,2,"Ich poste ein Bild von mir, in Badekleidung. ",FALSE
        |34,34,0,0,2,Ich gehe eine Kooperation mit einer Unterwäschebrand ein. ,FALSE
        |35,35,0,0,2,"Ich poste ein Bild von mir, in Unterwäsche. ",FALSE
        |36,36,0,-2,0,Generische Mobbing Karte,TRUE
        |37,30,0,0,0,<sender> erhält einen Ring,FALSE
        |38,38,0,0,1,Ich habe heute ein Bild von meinem lieblings-____ gepostet.,FALSE
        |39,39,0,0,1,Ich poste ein Selfie von mir. ,FALSE
        |40,40,0,0,1,Ich poste ein Selfie von mir. ,FALSE
        |41,41,0,0,1,Ich poste ein Bild von mir und meinem Haustier. ,FALSE
        |42,42,0,0,1,Ich poste ein Bild von mir und meinen Freunden. ,FALSE
        |43,43,0,0,1,Ich poste ein Bild von mir in meinem Urlaub. ,FALSE
        |44,44,0,0,1,Ich poste ein Bild von mir mit meinem neuen Partner/meiner neuen Partnerin. ,FALSE
        |45,45,0,0,1,Ich kommentiere das Bild eines Freundes/einer Freundin. ,FALSE
        |46,46,0,0,1,Ich like das Bild eines Freundes/einer Freundin.,FALSE
        |47,47,0,0,1,"Ich möchte ehrlich mit meinen Followern sein und meine dunkle Vergangenheit mit ihnen teilen, indem ich mich per Live-Video dazu äußere. ",FALSE
        |48,48,0,0,1,Social Media Detox! Ich möchte mir eine digitale Auszeit auf unbestimmte Zeit nehmen und informiere meine Follower darüber. ,FALSE
        |49,49,0,0,1,Ich wurde auf einem Bild mit einem bekannten Influencer/einer bekannten Influencerin markiert. ,FALSE
        |50,50,0,0,1,Mein Profil wurde gehackt. Eigenartige Nachrichten mit Links werden an meine Follower gesendet. ,FALSE
        |51,51,0,0,1,Ich gehe eine Kooperation mit einer Sportmarke ein. ,FALSE
        |52,52,0,0,1,Ich gehe eine Kooperation mit einer Beautymarke ein. ,FALSE
        |53,53,0,0,1,Ich gehe eine Kooperation mit einer Partei ein. ,FALSE
        |54,54,0,0,1,Ich gehe eine Kooperation mit einer Versicherungsanstalt ein. ,FALSE
        |55,55,0,0,1,Ich gehe eine Kooperation mit einem namhaften Technologiekonzern ein. ,FALSE
        |56,56,0,0,1,Ich gehe eine Kooperation mit einer Staubsaugermarke ein. ,FALSE
        |57,57,0,0,1,"Ich vergleiche mich mit anderen Influencern, um zu sehen, ob und wie ich meinen eigenen Auftritt optimieren kann. ",FALSE
        |58,58,0,0,1,Ich gehe vermehrt ins Fitnessstudio und achte auf eine gesunde Ernährung. ,FALSE
        |59,59,0,0,1,"In einem Live Video spreche ich mich zu den aktuellen politischen Vorkomnissen aus. Um authentisch zu bleiben, nehme ich kein Blatt vor den Mund. ",FALSE
        |60,60,0,0,1,"Ich poste ein Bild von mir, wie ich am Strand liege. ",FALSE
        |61,61,0,0,1,Ich beantworte die Nachrichten meiner Follower. ,FALSE
        |62,62,0,0,1,Ich gehe eine Kooperation mit einem anderen Influencer/einer anderen Influencerin ein. ,FALSE
        |63,63,0,0,1,In einer Story äußere ich mich kritisch zu einem Influencer/einer Influencerin. ,FALSE
        |64,64,0,0,1,Ich gehe eine Kooperation mit einer Fitnessbrand ein. ,FALSE
        |65,65,0,0,1,"Ich poste ein Bild von mir, in Badekleidung. ",FALSE
    """.trimMargin()


}