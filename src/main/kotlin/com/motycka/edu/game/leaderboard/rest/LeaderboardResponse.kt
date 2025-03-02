package com.motycka.edu.game.leaderboard.rest

import com.motycka.edu.game.character.rest.CharacterResponse

/**
 * Data Transfer Object (DTO) for returning leaderboard results.
 * This represents a single entry in the leaderboard, including ranking and match stats.
 */
data class LeaderboardResponse(
    val position: Int,  // The ranking position of the character
    val character: CharacterResponse,  // Character details including name, level, and experience
    val wins: Int,  // Number of matches won
    val losses: Int,  // Number of matches lost
    val draws: Int  // Number of matches ended in a draw
) {
    init {
        require(position > 0) { "Leaderboard position must be greater than zero." }
        require(wins >= 0) { "Wins cannot be negative." }
        require(losses >= 0) { "Losses cannot be negative." }
        require(draws >= 0) { "Draws cannot be negative." }
    }
}
