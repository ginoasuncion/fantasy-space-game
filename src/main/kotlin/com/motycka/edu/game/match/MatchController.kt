package com.motycka.edu.game.match.rest

import com.motycka.edu.game.match.service.MatchService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller for handling match-related API endpoints.
 * Exposes endpoints to retrieve and create matches.
 */
@RestController
@RequestMapping("/api/matches")
class MatchController(private val matchService: MatchService) {

    private val logger: Logger = LoggerFactory.getLogger(MatchController::class.java)

    /**
     * Retrieves all matches from the service.
     */
    @GetMapping
    fun getAllMatches(): ResponseEntity<List<MatchResponse>> {
        logger.info("Fetching all matches")
        return try {
            val matches = matchService.getAllMatches()
            ResponseEntity.ok(matches)
        } catch (e: Exception) {
            logger.error("Error fetching matches: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList())
        }
    }

    /**
     * Creates a new match based on the provided request.
     * Returns a **400 BAD REQUEST** if validation fails.
     */
    @PostMapping
    fun createMatch(@RequestBody request: MatchRequest): ResponseEntity<Any> {
        logger.info("Received request to create a match: {}", request)

        // ✅ Validation Checks
        if (request.rounds < 1) {
            return ResponseEntity.badRequest().body("Match must have at least one round.")
        }
        if (request.challengerId.isBlank() || request.opponentId.isBlank()) {
            return ResponseEntity.badRequest().body("Challenger ID and Opponent ID cannot be empty.")
        }

        return try {
            val match = matchService.createMatch(request)
            logger.info("Match created successfully: {}", match)
            ResponseEntity.ok(match)
        } catch (e: IllegalArgumentException) {
            logger.warn("Validation failed: ${e.message}")
            ResponseEntity.badRequest().body(e.message)
        } catch (e: Exception) {
            logger.error("Unexpected error while creating match: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create match.")
        }
    }
}
