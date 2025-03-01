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

    fun getAllMatches(): List<MatchResponse> {
        logger.info("Retrieving all matches")
        return matches.map { match ->
            MatchResponse(
                id = match.id.toString(),
                challenger = MatchCharacterResponse.fromCharacterResponse(
                    match.challenger, calculateExperience(match, match.challenger)
                ),
                opponent = MatchCharacterResponse.fromCharacterResponse(
                    match.opponent, calculateExperience(match, match.opponent)
                ),
                rounds = match.rounds,
                matchOutcome = match.matchOutcome
            )
        }
    }

    fun createMatch(request: MatchRequest): MatchResponse {
        logger.info("Validating match request: {}", request)
        val originalChallenger = characterService.getCharacterById(request.challengerId)
        val originalOpponent = characterService.getCharacterById(request.opponentId)

        if (!originalChallenger.isOwner) {
            logger.warn("Challenger must be owned by the user")
            throw IllegalArgumentException("Challenger must be owned by the user")
        }

        var challenger = originalChallenger.copy()
        var opponent = originalOpponent.copy()

        val rounds = mutableListOf<Round>()
        logger.info("Match started between {} and {}", challenger.name, opponent.name)

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

        val winner = determineWinner(challenger, opponent)
        val loser = if (winner == challenger) opponent else challenger
        val isDraw = winner == null

        val matchOutcome = determineMatchOutcome(winner, loser)

        logger.info("Match ended. Winner: {}, Loser: {}, Outcome: {}", winner?.name, loser?.name, matchOutcome)

        // ✅ Update Leaderboard Here
        leaderboardRepository.updateLeaderboard(winner?.id, loser?.id, isDraw)

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
                match.challenger, calculateExperience(match, match.challenger)
            ),
            opponent = MatchCharacterResponse.fromCharacterResponse(
                match.opponent, calculateExperience(match, match.opponent)
            ),
            rounds = match.rounds,
            matchOutcome = match.matchOutcome
        )
    }

    private fun determineWinner(challenger: CharacterResponse, opponent: CharacterResponse): CharacterResponse? {
        return when {
            challenger.health > 0 && opponent.health <= 0 -> challenger
            opponent.health > 0 && challenger.health <= 0 -> opponent
            else -> null
        }
    }

    private fun determineMatchOutcome(winner: CharacterResponse?, loser: CharacterResponse?): String {
        return when {
            winner == null -> "DRAW"
            winner == loser -> "DRAW"
            winner.id == winner.id -> "CHALLENGER_WON"
            else -> "OPPONENT_WON"
        }
    }

    private fun calculateExperience(match: Match, character: CharacterResponse): Int {
        return when (match.matchOutcome) {
            "CHALLENGER_WON" -> if (match.challenger.id == character.id) 100 else 50
            "OPPONENT_WON" -> if (match.opponent.id == character.id) 100 else 50
            else -> 75
        }
    }
}
