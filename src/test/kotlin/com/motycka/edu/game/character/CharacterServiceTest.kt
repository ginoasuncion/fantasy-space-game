package com.motycka.edu.game.character

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.character.model.CharacterClass
import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.error.NotFoundException
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CharacterServiceTest {

    private val characterRepository: CharacterRepository = mockk(relaxed = true)
    private val accountService: AccountService = mockk(relaxed = true)
    private lateinit var characterService: CharacterService

    @BeforeEach
    fun setup() {
        characterService = CharacterService(characterRepository, accountService)
    }

    @Test
    fun `getCharacterById should return character when found`() {
        val sampleCharacter = CharacterResponse(
            id = "1",
            name = "Sorcerer1",
            health = 100,
            attackPower = 60,
            stamina = null,
            defensePower = null,
            mana = 50,
            healingPower = 30,
            characterClass = CharacterClass.SORCERER,
            level = 3,
            experience = 1500,
            shouldLevelUp = false,
            isOwner = true
        )

        every { characterRepository.getCharacterById("1") } returns sampleCharacter

        val result = characterService.getCharacterById("1")

        assertNotNull(result)
        assertEquals("Sorcerer1", result.name)

        verify(exactly = 1) { characterRepository.getCharacterById("1") }
    }

    @Test
    fun `getCharacterById should throw NotFoundException when character is not found`() {
        // Given
        val characterId = "non_existent_id"
        every { characterRepository.getCharacterById(characterId) } returns null

        // When & Then (Expecting an Exception)
        val exception = assertThrows<NotFoundException> {
            characterService.getCharacterById(characterId)
        }

        // Verify exception message (Optional)
        assertEquals("Character with ID $characterId not found", exception.message)

        // Ensure the repository was actually called
        verify { characterRepository.getCharacterById(characterId) }
    }
}
