package com.motycka.edu.game.character

import com.motycka.edu.game.character.model.CharacterClass
import com.motycka.edu.game.character.rest.CharacterResponse
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CharacterControllerTest {

    private val characterService: CharacterService = mockk()
    private lateinit var characterController: CharacterController

    @BeforeEach
    fun setup() {
        characterController = CharacterController(characterService)
    }

    @Test
    fun `getAllCharacters should return list of characters`() {
        val sampleCharacters = listOf(
            CharacterResponse(
                id = "1",
                name = "Mage1",
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
        )

        every { characterService.getAllCharacters(null, null) } returns sampleCharacters

        val response: ResponseEntity<List<CharacterResponse>> = characterController.getAllCharacters(null, null)

        assertNotNull(response.body)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body!!.size)
        assertEquals("Mage1", response.body!![0].name)
    }

    @Test
    fun `getCharacterById should return character when found`() {
        val sampleCharacter = CharacterResponse(
            id = "1",
            name = "Knight1",
            health = 120,
            attackPower = 70,
            stamina = 30,
            defensePower = 40,
            mana = null,
            healingPower = null,
            characterClass = CharacterClass.WARRIOR,
            level = 4,
            experience = 2000,
            shouldLevelUp = false,
            isOwner = true
        )

        every { characterService.getCharacterById("1") } returns sampleCharacter

        val response = characterController.getCharacterById("1")

        assertNotNull(response.body)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Knight1", response.body!!.name)
    }
}
