package com.motycka.edu.game.match.model

import com.motycka.edu.game.character.rest.CharacterResponse

/**
 * Represents a match between two characters, including the rounds and outcome.
 */
data class Match(
    val id: Int,
    val challenger: CharacterResponse,
    val opponent: CharacterResponse,
    val rounds: List<Round>,
    val winner: CharacterResponse?,
    val loser: CharacterResponse?,
    val matchOutcome: String
)

/**
 * Represents the result of a round in a match.
 */
data class Round(
    val round: Int,
    val characterId: String,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int
)
