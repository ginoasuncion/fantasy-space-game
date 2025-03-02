package com.motycka.edu.game.leaderboard.service

import com.motycka.edu.game.leaderboard.repository.LeaderboardRepository
import com.motycka.edu.game.leaderboard.rest.LeaderboardResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service

@Service
class LeaderboardService(private val leaderboardRepository: LeaderboardRepository) {

    private val logger: Logger = LoggerFactory.getLogger(LeaderboardService::class.java)

    /**
     * Retrieves the leaderboard sorted by position.
     * Optional class filter can be applied.
     *
     * @param characterClass (Optional) Filter leaderboard by character class.
     * @return List of leaderboard entries sorted by wins.
     */
    fun getLeaderboard(characterClass: String?): List<LeaderboardResponse> {
        val entries = leaderboardRepository.getLeaderboard(characterClass)

        return entries.map { entry ->
            LeaderboardResponse(
                position = entry.position,
                character = entry.character,
                wins = entry.wins,
                losses = entry.losses,
                draws = entry.draws
            )
        }
    }


    /**
     * Updates the leaderboard after a match.
     * - Ensures no null values are passed for updates.
     * - Logs update operations for monitoring.
     *
     * @param winnerId ID of the match winner (nullable in case of a draw).
     * @param loserId ID of the match loser (nullable in case of a draw).
     * @param isDraw Boolean indicating whether the match ended in a draw.
     */
    fun updateLeaderboard(winnerId: String?, loserId: String?, isDraw: Boolean) {
        if (winnerId == null && loserId == null) {
            logger.warn("Invalid leaderboard update: Both winnerId and loserId are null.")
            return // Prevents accidental updates
        }

        logger.info("Updating leaderboard - Winner: {}, Loser: {}, Draw: {}", winnerId, loserId, isDraw)

        try {
            leaderboardRepository.updateLeaderboard(winnerId, loserId, isDraw)
            logger.info("Leaderboard updated successfully.")
        } catch (ex: DataAccessException) {
            logger.error("Error updating leaderboard: {}", ex.message)
        }
    }
}
