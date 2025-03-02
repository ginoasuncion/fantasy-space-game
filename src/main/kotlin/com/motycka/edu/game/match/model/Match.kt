package com.motycka.edu.game.match.model

import com.motycka.edu.game.character.rest.CharacterResponse

/**
 * Represents a match between two characters, including the rounds and outcome.
 * A match consists of a **challenger** and an **opponent** who engage in combat.
 */
data class Match(
    val id: Int,
    val challenger: CharacterResponse,
    val opponent: CharacterResponse,
    val rounds: List<Round>,
    val winner: CharacterResponse?,  // Nullable: Can be null in case of a draw
    val loser: CharacterResponse?,   // Nullable: Can be null in case of a draw
    val matchOutcome: String  // Stores outcome: "CHALLENGER_WON", "OPPONENT_WON", or "DRAW"
) {
    init {
        require(id > 0) { "Match ID must be a positive integer." }
        require(challenger.id != opponent.id) { "Challenger and Opponent must be different characters." }
    }
}

/**
 * Represents the result of a single round in a match.
 * Each round records the **damage dealt**, **stamina changes**, and **mana changes**.
 */
data class Round(
    val round: Int,
    val characterId: String,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int
) {
    init {
        require(round > 0) { "Round number must be greater than zero." }
        require(healthDelta <= 0) { "Health delta must be negative or zero (damage or no effect)." }
    }
}
