package com.motycka.edu.game.leaderboard.model

import com.motycka.edu.game.character.rest.CharacterResponse

/**
 * Represents an entry in the leaderboard, tracking a character's match history.
 */
data class LeaderboardEntry(
    val position: Int,
    val character: CharacterResponse,
    val wins: Int,
    val losses: Int,
    val draws: Int
) {
    init {
        require(position >= 0) { "Position cannot be negative" }
        require(wins >= 0) { "Wins cannot be negative" }
        require(losses >= 0) { "Losses cannot be negative" }
        require(draws >= 0) { "Draws cannot be negative" }
    }
}
