package com.motycka.edu.game.character

import com.motycka.edu.game.character.model.CharacterClass
import com.motycka.edu.game.character.rest.CharacterResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(SpringExtension::class)
class CharacterRepositoryTest {

    private val jdbcTemplate: JdbcTemplate = mockk(relaxed = true)
    private lateinit var characterRepository: CharacterRepository

    @BeforeEach
    fun setup() {
        characterRepository = CharacterRepository(jdbcTemplate)
    }

    @Test
    fun `getFilteredCharacters should return all characters when no filter is applied`() {
        // Prepare sample data
        val sampleCharacter = CharacterResponse(
            id = "1",
            name = "Knight",
            health = 100,
            attackPower = 40,
            stamina = 20,
            defensePower = 30,
            mana = null,
            healingPower = null,
            characterClass = CharacterClass.WARRIOR,
            level = 5,
            experience = 500,
            shouldLevelUp = false,
            isOwner = true
        )

        // Mock the SQL call
        every {
            jdbcTemplate.query(
                match<String> { it.contains("SELECT * FROM character WHERE 1=1") },
                any<RowMapper<CharacterResponse>>(),
                *anyVararg()
            )
        } returns listOf(sampleCharacter)

        // Call repository method
        val result = characterRepository.getFilteredCharacters(null, null)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("Knight", result[0].name)

        // Verify query invocation
        verify(exactly = 1) {
            jdbcTemplate.query(
                match<String> { it.contains("SELECT * FROM character WHERE 1=1") },
                any<RowMapper<CharacterResponse>>(),
                *anyVararg()
            )
        }
    }

    @Test
    fun `getFilteredCharacters should return filtered characters by class`() {
        val warrior = CharacterResponse(
            id = "1",
            name = "Warrior",
            health = 120,
            attackPower = 50,
            stamina = 25,
            defensePower = 40,
            mana = null,
            healingPower = null,
            characterClass = CharacterClass.WARRIOR,
            level = 6,
            experience = 700,
            shouldLevelUp = false,
            isOwner = true
        )

        // Mock the SQL call for class filter
        every {
            jdbcTemplate.query(
                match<String> { it.contains("AND class = ?") },
                any<RowMapper<CharacterResponse>>(),
                *anyVararg()
            )
        } returns listOf(warrior)

        val result = characterRepository.getFilteredCharacters(CharacterClass.WARRIOR, null)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(CharacterClass.WARRIOR, result[0].characterClass)

        verify(exactly = 1) {
            jdbcTemplate.query(
                match<String> { it.contains("AND class = ?") },
                any<RowMapper<CharacterResponse>>(),
                *anyVararg()
            )
        }
    }

    @Test
    fun `getFilteredCharacters should return filtered characters by name`() {
        val mage = CharacterResponse(
            id = "2",
            name = "Mage Master",
            health = 80,
            attackPower = 60,
            stamina = null,
            defensePower = 20,
            mana = 100,
            healingPower = 40,
            characterClass = CharacterClass.SORCERER,
            level = 7,
            experience = 900,
            shouldLevelUp = false,
            isOwner = false
        )

        // Mock the SQL call for name filter
        every {
            jdbcTemplate.query(
                match<String> { it.contains("AND LOWER(name) LIKE ?") },
                any<RowMapper<CharacterResponse>>(),
                *anyVararg()
            )
        } returns listOf(mage)

        val result = characterRepository.getFilteredCharacters(null, "Mage")

        assertNotNull(result)
        assertEquals(1, result.size)
        assertTrue(result[0].name.contains("Mage"))

        verify(exactly = 1) {
            jdbcTemplate.query(
                match<String> { it.contains("AND LOWER(name) LIKE ?") },
                any<RowMapper<CharacterResponse>>(),
                *anyVararg()
            )
        }
    }
}
