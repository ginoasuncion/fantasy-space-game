package com.motycka.edu.game.match.rest

/**
 * Data Transfer Object (DTO) for creating a match request.
 * This DTO is used to receive the **user's match setup request**.
 */
data class MatchRequest(
    val rounds: Int,
    val challengerId: String,
    val opponentId: String
) {
    init {
        require(rounds > 0) { "Match must have at least 1 round." }
        require(challengerId.isNotBlank()) { "Challenger ID must not be empty." }
        require(opponentId.isNotBlank()) { "Opponent ID must not be empty." }
        require(challengerId != opponentId) { "Challenger and Opponent must be different characters." }
    }
}
