package com.motycka.edu.game.character

import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.character.model.CharacterClass
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * Repository class for handling database operations related to characters.
 */
@Repository
class CharacterRepository(private val jdbcTemplate: JdbcTemplate) {

    /**
     * Row mapper to map SQL query results to CharacterResponse objects.
     */
    private val rowMapper = RowMapper { rs, _ ->
        CharacterResponse(
            id = rs.getString("id"),
            name = rs.getString("name"),
            health = rs.getInt("health"),
            attackPower = rs.getInt("attack"),
            stamina = rs.getObject("stamina") as? Int,
            defensePower = rs.getObject("defense") as? Int,
            mana = rs.getObject("mana") as? Int,
            healingPower = rs.getObject("healing") as? Int,
            characterClass = CharacterClass.valueOf(rs.getString("class")),
            level = rs.getInt("level"),
            experience = rs.getInt("experience"),
            shouldLevelUp = rs.getBoolean("should_level_up"),
            isOwner = rs.getBoolean("is_owner"),
            isVictor = false
        )
    }

    /**
     * Retrieves all characters with optional filtering by class and name.
     */
    fun getFilteredCharacters(characterClass: CharacterClass?, name: String?): List<CharacterResponse> {
        val sqlBuilder = StringBuilder("SELECT * FROM character WHERE 1=1")
        val params = mutableListOf<Any>()

        if (characterClass != null) {
            sqlBuilder.append(" AND class = ?")
            params.add(characterClass.name)
        }

        if (!name.isNullOrBlank()) {
            sqlBuilder.append(" AND LOWER(name) LIKE ?")
            params.add("%${name.lowercase()}%")
        }

        return jdbcTemplate.query(sqlBuilder.toString(), rowMapper, *params.toTypedArray())
    }

    /**
     * Retrieves a character by its ID.
     */
    fun getCharacterById(id: String): CharacterResponse? {
        return try {
            val sql = "SELECT * FROM character WHERE id = ?"
            jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
        } catch (e: DataAccessException) {
            throw RuntimeException("Error fetching character with ID: $id", e)
        }
    }

    /**
     * Creates a new character in the database.
     */
    fun createCharacter(character: CharacterResponse, accountId: String) {
        try {
            val sql = """
                INSERT INTO character (id, account_id, name, health, attack, stamina, defense, mana, healing, class, level, experience, should_level_up, is_owner) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
            jdbcTemplate.update(
                sql, character.id, accountId, character.name, character.health, character.attackPower,
                character.stamina, character.defensePower, character.mana, character.healingPower,
                character.characterClass.name, character.level, character.experience, character.shouldLevelUp, true
            )
        } catch (e: DataAccessException) {
            throw RuntimeException("Error creating character: ${character.name}", e)
        }
    }

    /**
     * Updates an existing character's attributes.
     */
    fun updateCharacter(character: CharacterResponse) {
        try {
            val sql = """
                UPDATE character SET name = ?, health = ?, attack = ?, stamina = ?, defense = ?, mana = ?, healing = ?, 
                class = ?, level = ?, experience = ?, should_level_up = ? WHERE id = ?
            """
            jdbcTemplate.update(
                sql, character.name, character.health, character.attackPower, character.stamina,
                character.defensePower, character.mana, character.healingPower, character.characterClass.name,
                character.level, character.experience, character.shouldLevelUp, character.id
            )
        } catch (e: DataAccessException) {
            throw RuntimeException("Error updating character with ID: ${character.id}", e)
        }
    }

    /**
     * Updates only the experience points of a character.
     */
    fun updateCharacterExperience(characterId: String, newExperience: Int) {
        try {
            val sql = "UPDATE character SET experience = ? WHERE id = ?"
            val rowsUpdated = jdbcTemplate.update(sql, newExperience, characterId)

            if (rowsUpdated == 0) {
                throw IllegalArgumentException("Character with ID $characterId not found")
            }
        } catch (e: DataAccessException) {
            throw RuntimeException("Error updating experience for character ID: $characterId", e)
        }
    }

    /**
     * Retrieves all characters owned by a specific account (challengers).
     */
    fun getChallengers(accountId: String): List<CharacterResponse> {
        return try {
            val sql = "SELECT * FROM character WHERE account_id = ?"
            jdbcTemplate.query(sql, rowMapper, accountId)
        } catch (e: DataAccessException) {
            throw RuntimeException("Error fetching challengers for account ID: $accountId", e)
        }
    }

    /**
     * Retrieves all characters that are not owned by the current account (opponents).
     */
    fun getOpponents(accountId: String): List<CharacterResponse> {
        return try {
            val sql = "SELECT * FROM character WHERE account_id != ?"
            jdbcTemplate.query(sql, rowMapper, accountId)
        } catch (e: DataAccessException) {
            throw RuntimeException("Error fetching opponents for account ID: $accountId", e)
        }
    }
}
