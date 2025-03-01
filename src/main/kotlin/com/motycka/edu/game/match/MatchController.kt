package com.motycka.edu.game.match.rest

import com.motycka.edu.game.match.service.MatchService
import com.motycka.edu.game.match.rest.MatchRequest
import com.motycka.edu.game.match.rest.MatchResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/matches")
class MatchController(private val matchService: MatchService) {

    private val logger: Logger = LoggerFactory.getLogger(MatchController::class.java)

    @GetMapping
    fun getAllMatches(): ResponseEntity<List<MatchResponse>> {
        logger.info("Fetching all matches")
        val matches = matchService.getAllMatches()
        return ResponseEntity.ok(matches)
    }

    @PostMapping
    fun createMatch(@RequestBody request: MatchRequest): ResponseEntity<MatchResponse> {
        logger.info("Received request to create a match: {}", request)
        val match = matchService.createMatch(request)
        logger.info("Match created successfully: {}", match)
        return ResponseEntity.ok(match)
    }
}
