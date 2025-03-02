package com.motycka.edu.game.leaderboard.rest

import com.motycka.edu.game.leaderboard.service.LeaderboardService
import io.mockk.*
import org.junit.jupiter.api.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LeaderboardControllerTest {

    private lateinit var leaderboardController: LeaderboardController
    private val leaderboardService: LeaderboardService = mockk()

    @BeforeEach
    fun setUp() {
        leaderboardController = LeaderboardController(leaderboardService)
        clearAllMocks()
    }

    @Test
    fun `getLeaderboard should return leaderboard results`() {
        // Given
        val expectedLeaderboard = listOf(sampleLeaderboardEntry(1))
        every { leaderboardService.getLeaderboard(null) } returns expectedLeaderboard

        // When
        val response: ResponseEntity<Any> = leaderboardController.getLeaderboard(null)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body is List<*>)  // Ensure it's a list
        assertEquals(expectedLeaderboard, response.body)
    }

    @Test
    fun `getLeaderboard should return NOT_FOUND when leaderboard is empty`() {
        // Given
        every { leaderboardService.getLeaderboard(null) } returns emptyList()

        // When
        val response: ResponseEntity<Any> = leaderboardController.getLeaderboard(null)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("No leaderboard entries found.", response.body)
    }

    @Test
    fun `getLeaderboard should return BAD_REQUEST for invalid character class`() {
        // Given
        every { leaderboardService.getLeaderboard("INVALID") } throws IllegalArgumentException("Invalid class")

        // When
        val response: ResponseEntity<Any> = leaderboardController.getLeaderboard("INVALID")

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid character class filter.", response.body)
    }

    @Test
    fun `getLeaderboard should return INTERNAL_SERVER_ERROR on unexpected error`() {
        // Given
        every { leaderboardService.getLeaderboard(null) } throws RuntimeException("Unexpected error")

        // When
        val response: ResponseEntity<Any> = leaderboardController.getLeaderboard(null)

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("An error occurred while fetching the leaderboard.", response.body)
    }

    // Helper function
    private fun sampleLeaderboardEntry(position: Int) = LeaderboardResponse(
        position = position,
        character = mockk(),
        wins = 5,
        losses = 2,
        draws = 1
    )
}
