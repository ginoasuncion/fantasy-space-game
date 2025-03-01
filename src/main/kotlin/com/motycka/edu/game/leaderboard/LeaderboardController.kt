package com.motycka.edu.game.leaderboard.rest

import com.motycka.edu.game.leaderboard.service.LeaderboardService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/leaderboards")
class LeaderboardController(private val leaderboardService: LeaderboardService) {

    private val logger: Logger = LoggerFactory.getLogger(LeaderboardController::class.java)

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) characterClass: String?): ResponseEntity<List<LeaderboardResponse>> {
        logger.info("Fetching leaderboard with class filter: {}", characterClass)
        val leaderboard = leaderboardService.getLeaderboard(characterClass)
        logger.info("Returning {} leaderboard entries", leaderboard.size)
        return ResponseEntity.ok(leaderboard)
    }
}
