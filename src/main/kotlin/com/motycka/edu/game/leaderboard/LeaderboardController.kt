package com.motycka.edu.game.leaderboard.rest

import com.motycka.edu.game.leaderboard.service.LeaderboardService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/leaderboards")
class LeaderboardController(private val leaderboardService: LeaderboardService) {

    private val logger: Logger = LoggerFactory.getLogger(LeaderboardController::class.java)

    /**
     * Fetches the leaderboard with an optional filter by character class.
     * @param characterClass Optional character class filter (e.g., "WARRIOR" or "SORCERER").
     * @return A list of leaderboard entries sorted by ranking.
     */
    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) characterClass: String?): ResponseEntity<Any> {
        logger.info("Fetching leaderboard with class filter: {}", characterClass)

        return try {
            val leaderboard = leaderboardService.getLeaderboard(characterClass)
            if (leaderboard.isEmpty()) {
                logger.warn("No leaderboard entries found for class filter: {}", characterClass)
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("No leaderboard entries found.")
            } else {
                logger.info("Returning {} leaderboard entries", leaderboard.size)
                ResponseEntity.ok(leaderboard)
            }
        } catch (e: IllegalArgumentException) {
            logger.error("Invalid character class filter: {}", characterClass, e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid character class filter.")
        } catch (e: Exception) {
            logger.error("Unexpected error while fetching leaderboard", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the leaderboard.")
        }
    }
}
