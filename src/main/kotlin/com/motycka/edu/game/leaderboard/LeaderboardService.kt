package com.motycka.edu.game.leaderboard.service

import com.motycka.edu.game.leaderboard.repository.LeaderboardRepository
import com.motycka.edu.game.leaderboard.rest.LeaderboardResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LeaderboardService(private val leaderboardRepository: LeaderboardRepository) {

    private val logger: Logger = LoggerFactory.getLogger(LeaderboardService::class.java)

    /**
     * Retrieves the leaderboard sorted by position and filtered by class if provided.
     */
    fun getLeaderboard(characterClass: String?): List<LeaderboardResponse> {
        logger.info("Fetching leaderboard for class filter: {}", characterClass)
        return leaderboardRepository.getLeaderboard(characterClass)
    }

    /**
     * Updates the leaderboard after a match outcome.
     */
    fun updateLeaderboard(winnerId: String?, loserId: String?, isDraw: Boolean) {
        logger.info("Updating leaderboard - Winner: {}, Loser: {}, Draw: {}", winnerId, loserId, isDraw)
        leaderboardRepository.updateLeaderboard(winnerId, loserId, isDraw)
    }
}
