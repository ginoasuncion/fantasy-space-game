package com.motycka.edu.game.match.rest

import com.motycka.edu.game.match.service.MatchService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class MatchControllerTest {

    private lateinit var matchController: MatchController
    private val matchService: MatchService = mockk()

    @BeforeEach
    fun setUp() {
        matchController = MatchController(matchService)
    }

    @Test
    fun `getAllMatches should return list of matches`() {
        // Given
        val mockMatches = listOf(mockk<MatchResponse>(relaxed = true), mockk<MatchResponse>(relaxed = true))
        every { matchService.getAllMatches() } returns mockMatches

        // When
        val response: ResponseEntity<List<MatchResponse>> = matchController.getAllMatches()

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(2, response.body?.size)
        verify(exactly = 1) { matchService.getAllMatches() }
    }

    @Test
    fun `getAllMatches should return empty list when no matches exist`() {
        // Given
        every { matchService.getAllMatches() } returns emptyList()

        // When
        val response: ResponseEntity<List<MatchResponse>> = matchController.getAllMatches()

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body?.isEmpty() == true)
        verify(exactly = 1) { matchService.getAllMatches() }
    }

    @Test
    fun `createMatch should return match response when valid request is provided`() {
        // Given
        val request = MatchRequest(rounds = 3, challengerId = "char1", opponentId = "char2")
        val mockMatchResponse = mockk<MatchResponse>(relaxed = true)

        every { matchService.createMatch(request) } returns mockMatchResponse

        // When
        val response = matchController.createMatch(request)

        // Then
        assertEquals(HttpStatus.OK, (response as ResponseEntity<*>).statusCode)
        assertEquals(mockMatchResponse, response.body)
        verify(exactly = 1) { matchService.createMatch(request) }
    }

    @Test
    fun `createMatch should return BAD_REQUEST when service throws IllegalArgumentException`() {
        // Given
        val request = MatchRequest(rounds = 3, challengerId = "char1", opponentId = "char2")
        every { matchService.createMatch(request) } throws IllegalArgumentException("Invalid match")

        // When
        val response = matchController.createMatch(request)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, (response as ResponseEntity<*>).statusCode)
        assertEquals("Invalid match", response.body)
        verify(exactly = 1) { matchService.createMatch(request) }
    }

    @Test
    fun `createMatch should return INTERNAL_SERVER_ERROR when service throws unexpected error`() {
        // Given
        val request = MatchRequest(rounds = 3, challengerId = "char1", opponentId = "char2")
        every { matchService.createMatch(request) } throws RuntimeException("Unexpected error")

        // When
        val response = matchController.createMatch(request)

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, (response as ResponseEntity<*>).statusCode)
        assertEquals("Failed to create match.", response.body)
        verify(exactly = 1) { matchService.createMatch(request) }
    }
}
