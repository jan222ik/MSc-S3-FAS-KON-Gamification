package com.github.jan222ik.gamification.ui.logic

data class RoundHistory(
    val round: Int,
    val effectsPerPlayer: Map<Player, Int>,
    val effects: List<PlayedCard>
) {
    override fun toString(): String {
        return "RoundHistory(\n" +
                "\tround=$round,\n" +
                "\teffectsPerPlayer=[\n" +
                "\t\t${
                    effectsPerPlayer.entries.joinToString(",\n\t\t")
                }\n" +
                "\t],\n" +
                "\teffects=[\n" +
                "\t\t${effects.joinToString(",\n\t\t")}\n" +
                "\t]\n" +
                ")"
    }
}