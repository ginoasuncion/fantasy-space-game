package com.motycka.edu.game.leaderboard.model

import com.motycka.edu.game.character.rest.CharacterResponse

/**
 * Represents an entry in the leaderboard.
 */
data class LeaderboardEntry(
    val position: Int,
    val character: CharacterResponse,
    val wins: Int,
    val losses: Int,
    val draws: Int
)
