package com.motycka.edu.game.match.rest

import com.motycka.edu.game.match.model.Round
import com.motycka.edu.game.character.rest.CharacterResponse

/**
 * Data Transfer Object (DTO) for match response.
 * This DTO is used to send match results **back to the client**.
 */
data class MatchResponse(
    val id: String,
    val challenger: MatchCharacterResponse,
    val opponent: MatchCharacterResponse,
    val rounds: List<Round>,
    val matchOutcome: String
) {
    init {
        require(id.isNotBlank()) { "Match ID cannot be empty." }
        require(rounds.isNotEmpty()) { "A match must have at least one round." }
        require(matchOutcome in listOf("CHALLENGER_WON", "OPPONENT_WON", "DRAW")) {
            "Invalid match outcome: $matchOutcome"
        }
    }
}

/**
 * Represents a character's match-related data.
 * This object contains details about **each character's performance** in the match.
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
        /**
         * Factory method to convert a **CharacterResponse** into **MatchCharacterResponse**.
         * This method ensures **data consistency** when transferring match-related character data.
         */
        fun fromCharacterResponse(character: CharacterResponse, experienceGained: Int): MatchCharacterResponse {
            require(character.id.isNotBlank()) { "Character ID cannot be empty." }
            require(experienceGained >= 0) { "Experience gained cannot be negative." }

            return MatchCharacterResponse(
                id = character.id,
                name = character.name,
                characterClass = character.characterClass.name,
                level = character.level,
                experienceTotal = character.experience,
                experienceGained = experienceGained
            )
        }
    }
}
