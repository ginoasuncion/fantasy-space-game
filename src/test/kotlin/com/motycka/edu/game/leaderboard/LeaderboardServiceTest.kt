package com.motycka.edu.game.leaderboard.service

import com.motycka.edu.game.leaderboard.repository.LeaderboardRepository
import com.motycka.edu.game.leaderboard.rest.LeaderboardResponse
import io.mockk.*
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LeaderboardServiceTest {

    private lateinit var leaderboardService: LeaderboardService
    private val leaderboardRepository: LeaderboardRepository = mockk()

    @BeforeEach
    fun setUp() {
        leaderboardService = LeaderboardService(leaderboardRepository)
        clearAllMocks()
    }

    @Test
    fun `getLeaderboard should return leaderboard results`() {
        // Given
        val expected = listOf(sampleLeaderboardEntry(1))
        every { leaderboardRepository.getLeaderboard(null) } returns expected

        // When
        val result = leaderboardService.getLeaderboard(null)

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun `updateLeaderboard should correctly call repository methods`() {
        every { leaderboardRepository.updateLeaderboard("winnerId", "loserId", false) } just Runs

        leaderboardService.updateLeaderboard("winnerId", "loserId", false)

        verify { leaderboardRepository.updateLeaderboard("winnerId", "loserId", false) }
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
