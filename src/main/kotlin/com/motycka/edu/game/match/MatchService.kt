package com.motycka.edu.game.match.service

import com.motycka.edu.game.character.CharacterService
import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.leaderboard.repository.LeaderboardRepository
import com.motycka.edu.game.match.model.Match
import com.motycka.edu.game.match.model.Round
import com.motycka.edu.game.match.rest.MatchCharacterResponse
import com.motycka.edu.game.match.rest.MatchRequest
import com.motycka.edu.game.match.rest.MatchResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class MatchService(
    private val characterService: CharacterService,
    private val leaderboardRepository: LeaderboardRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(MatchService::class.java)
    private val matches = mutableListOf<Match>()
    private val matchIdGenerator = AtomicInteger(1)

    /**
     * Retrieves all matches with calculated experience points.
     */
    fun getAllMatches(): List<MatchResponse> {
        logger.info("Retrieving all matches")
        return matches.map { match ->
            MatchResponse(
                id = match.id.toString(),
                challenger = MatchCharacterResponse.fromCharacterResponse(
                    match.challenger, calculateExperience(match.matchOutcome).first
                ),
                opponent = MatchCharacterResponse.fromCharacterResponse(
                    match.opponent, calculateExperience(match.matchOutcome).second
                ),
                rounds = match.rounds,
                matchOutcome = match.matchOutcome
            )
        }
    }

    /**
     * Creates a new match between two characters.
     * Validates inputs and applies match rules.
     */
    fun createMatch(request: MatchRequest): MatchResponse {
        logger.info("Validating match request: {}", request)

        // Prevent Self-Matches
        if (request.challengerId == request.opponentId) {
            logger.warn("A character cannot fight themselves!")
            throw IllegalArgumentException("Challenger and Opponent must be different characters.")
        }

        // Retrieve Characters
        val originalChallenger = characterService.getCharacterById(request.challengerId)
        val originalOpponent = characterService.getCharacterById(request.opponentId)

        if (!originalChallenger.isOwner) {
            logger.warn("Challenger must be owned by the user")
            throw IllegalArgumentException("Challenger must be owned by the user")
        }

        // Copy Characters for Match Calculations
        var challenger = originalChallenger.copy()
        var opponent = originalOpponent.copy()

        val rounds = mutableListOf<Round>()
        logger.info("Match started between {} and {}", challenger.name, opponent.name)

        try {
            // 🔄 **Simulate Rounds**
            for (roundNumber in 1..request.rounds) {
                if (challenger.health <= 0 || opponent.health <= 0) break

                val challengerDamage = maxOf((challenger.attackPower ?: 0) - ((opponent.defensePower ?: 0) / 5), 0)
                opponent = opponent.copy(health = (opponent.health - challengerDamage).coerceAtLeast(0))
                rounds.add(Round(roundNumber, opponent.id, -challengerDamage, -5, 0))
                logger.info("Round {}: {} attacks {} for {} damage (Remaining HP: {})",
                    roundNumber, challenger.name, opponent.name, challengerDamage, opponent.health)

                if (opponent.health <= 0) {
                    logger.info("{} has been defeated!", opponent.name)
                    break
                }

                val opponentDamage = maxOf((opponent.attackPower ?: 0) - ((challenger.defensePower ?: 0) / 5), 0)
                challenger = challenger.copy(health = (challenger.health - opponentDamage).coerceAtLeast(0))
                rounds.add(Round(roundNumber, challenger.id, -opponentDamage, 0, -10))
                logger.info("Round {}: {} counterattacks {} for {} damage (Remaining HP: {})",
                    roundNumber, opponent.name, challenger.name, opponentDamage, challenger.health)

                if (challenger.health <= 0) {
                    logger.info("{} has been defeated!", challenger.name)
                    break
                }
            }

            // Determine Winner
            val winner = determineWinner(challenger, opponent)
            val loser = if (winner == challenger) opponent else challenger
            val isDraw = winner == null
            val matchOutcome = determineMatchOutcome(winner, request.challengerId)

            logger.info("Match ended. Winner: {}, Loser: {}, Outcome: {}", winner?.name, loser?.name, matchOutcome)

            // Update Leaderboard
            leaderboardRepository.updateLeaderboard(winner?.id, loser?.id, isDraw)

            // Update Experience
            val (challengerXP, opponentXP) = calculateExperience(matchOutcome)
            characterService.updateExperience(request.challengerId, challengerXP)
            characterService.updateExperience(request.opponentId, opponentXP)

            // **Create Match Record**
            val match = Match(
                id = matchIdGenerator.getAndIncrement(),
                challenger = originalChallenger,
                opponent = originalOpponent,
                rounds = rounds,
                winner = winner,
                loser = loser,
                matchOutcome = matchOutcome
            )

            matches.add(match)
            logger.info("Match successfully created: {}", match)

            return MatchResponse(
                id = match.id.toString(),
                challenger = MatchCharacterResponse.fromCharacterResponse(
                    match.challenger.copy(health = match.challenger.health.coerceAtLeast(0)), // ✅ Ensure non-negative health
                    challengerXP
                ),
                opponent = MatchCharacterResponse.fromCharacterResponse(
                    match.opponent.copy(health = match.opponent.health.coerceAtLeast(0)), // ✅ Ensure non-negative health
                    opponentXP
                ),
                rounds = match.rounds,
                matchOutcome = match.matchOutcome
            )

        } catch (e: Exception) {
            logger.error("Error occurred during match processing: ${e.message}")
            throw IllegalStateException("An error occurred while processing the match.")
        }
    }

    /**
     * Determines the winner based on health values.
     * A draw occurs if both characters reach 0 HP.
     */
    private fun determineWinner(challenger: CharacterResponse, opponent: CharacterResponse): CharacterResponse? {
        return when {
            challenger.health == 0 && opponent.health == 0 -> null // Draw
            challenger.health > 0 -> challenger
            opponent.health > 0 -> opponent
            else -> null
        }
    }

    /**
     * Determines the match outcome string.
     */
    private fun determineMatchOutcome(winner: CharacterResponse?, challengerId: String): String {
        return when {
            winner == null -> "DRAW"
            winner.id == challengerId -> "CHALLENGER_WON"
            else -> "OPPONENT_WON"
        }
    }

    /**
     * Determines experience gained based on match outcome.
     */
    private fun calculateExperience(matchOutcome: String): Pair<Int, Int> {
        return when (matchOutcome) {
            "CHALLENGER_WON" -> Pair(100, 50)
            "OPPONENT_WON" -> Pair(50, 100)
            "DRAW" -> Pair(75, 75)
            else -> Pair(0, 0)
        }
    }
}
