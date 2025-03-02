package com.motycka.edu.game.match.service

import com.motycka.edu.game.character.CharacterService
import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.character.model.CharacterClass
import com.motycka.edu.game.leaderboard.repository.LeaderboardRepository
import com.motycka.edu.game.match.model.Match
import com.motycka.edu.game.match.model.Round
import com.motycka.edu.game.match.rest.MatchRequest
import com.motycka.edu.game.match.rest.MatchResponse
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MatchServiceTest {

    private lateinit var matchService: MatchService

    private val characterService: CharacterService = mockk()
    private val leaderboardRepository: LeaderboardRepository = mockk()

    @BeforeEach
    fun setUp() {
        matchService = MatchService(characterService, leaderboardRepository)
        // Clear mocks between tests
        clearAllMocks()
    }

    /**
     * createMatch should throw exception if an error occurs during processing
     */
    @Test
    fun `should throw exception if an error occurs during processing`() {
        // Given
        val request = MatchRequest(rounds = 3, challengerId = "char1", opponentId = "char2")
        val challenger = sampleCharacter(id = "char1", isOwner = true)
        val opponent = sampleCharacter(id = "char2")

        every { characterService.getCharacterById("char1") } returns challenger
        every { characterService.getCharacterById("char2") } returns opponent
        // Simulate an error in experience update
        every { characterService.updateExperience(any(), any()) } throws RuntimeException("DB error")

        // Then
        val exception = assertThrows<IllegalStateException> {
            matchService.createMatch(request)
        }
        // Check message
        assertEquals("An error occurred while processing the match.", exception.message)
    }

    // Helper method to create sample CharacterResponse objects
    private fun sampleCharacter(
        id: String,
        isOwner: Boolean = false,
        health: Int = 100,
        attackPower: Int = 20,
        defensePower: Int = 10
    ) = CharacterResponse(
        id = id,
        name = "Char-$id",
        health = health,
        attackPower = attackPower,
        stamina = null,
        defensePower = defensePower,
        mana = null,
        healingPower = null,
        characterClass = CharacterClass.WARRIOR, // or SORCERER
        level = 1,
        experience = 0,
        shouldLevelUp = false,
        isOwner = isOwner
    )
}
