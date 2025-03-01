package com.motycka.edu.game.match.rest

import com.motycka.edu.game.match.model.Round
import com.motycka.edu.game.character.rest.CharacterResponse

/**
 * Data Transfer Object for match response.
 */
data class MatchResponse(
    val id: String,
    val challenger: MatchCharacterResponse,
    val opponent: MatchCharacterResponse,
    val rounds: List<Round>,
    val matchOutcome: String
)

/**
 * Represents a character's match-related data.
 */
data class MatchCharacterResponse(
    val id: String,
    val name: String,
    val characterClass: String,
    val level: Int,
    val experienceTotal: Int,
    val experienceGained: Int
) {
    companion object {
        fun fromCharacterResponse(character: CharacterResponse, experienceGained: Int) = MatchCharacterResponse(
            id = character.id,
            name = character.name,
            characterClass = character.characterClass.name,
            level = character.level,
            experienceTotal = character.experience,
            experienceGained = experienceGained
        )
    }
}
