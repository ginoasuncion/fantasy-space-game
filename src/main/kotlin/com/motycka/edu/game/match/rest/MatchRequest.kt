package com.motycka.edu.game.match.rest

/**
 * Data Transfer Object for creating a match.
 */
data class MatchRequest(
    val rounds: Int,
    val challengerId: String,
    val opponentId: String
)