package com.motycka.edu.game.leaderboard.repository

import com.motycka.edu.game.leaderboard.model.LeaderboardEntry
import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.character.model.CharacterClass
import io.mockk.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

class LeaderboardRepositoryTest {

    private lateinit var leaderboardRepository: LeaderboardRepository
    private val jdbcTemplate: JdbcTemplate = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        leaderboardRepository = LeaderboardRepository(jdbcTemplate)
    }

    @Test
    fun `getLeaderboard should return leaderboard results`() {
        // Mock Database Result
        val mockLeaderboardEntry = listOf(
            LeaderboardEntry(
                position = 1,
                character = CharacterResponse(
                    id = "char1", name = "Warrior", health = 100, attackPower = 50, stamina = 10,
                    defensePower = 5, mana = null, healingPower = null, characterClass = CharacterClass.WARRIOR,
                    level = 5, experience = 200, shouldLevelUp = false, isOwner = false
                ),
                wins = 10, losses = 2, draws = 1
            )
        )

        every { jdbcTemplate.query(any<String>(), any<RowMapper<LeaderboardEntry>>()) } returns mockLeaderboardEntry

        val result = leaderboardRepository.getLeaderboard(null)

        assertEquals(1, result.size)
        assertEquals("Warrior", result[0].character.name)
        assertEquals(10, result[0].wins)
        verify(exactly = 1) { jdbcTemplate.query(any<String>(), any<RowMapper<LeaderboardEntry>>()) }
    }

    @Test
    fun `getLeaderboard should return empty list when no data is found`() {
        every { jdbcTemplate.query(any<String>(), any<RowMapper<LeaderboardEntry>>()) } returns emptyList()

        val result = leaderboardRepository.getLeaderboard(null)

        assertTrue(result.isEmpty())
        verify(exactly = 1) { jdbcTemplate.query(any<String>(), any<RowMapper<LeaderboardEntry>>()) }
    }
}
