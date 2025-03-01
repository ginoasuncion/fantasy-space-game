package com.motycka.edu.game.character.repository

import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.character.model.CharacterClass
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class CharacterRepository(private val jdbcTemplate: JdbcTemplate) {

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

    fun getAllCharacters(): List<CharacterResponse> {
        val sql = "SELECT * FROM character"
        return jdbcTemplate.query(sql, rowMapper)
    }

    fun getCharacterById(id: String): CharacterResponse? {
        val sql = "SELECT * FROM character WHERE id = ?"
        return jdbcTemplate.query(sql, rowMapper, id).firstOrNull()
    }

    fun createCharacter(character: CharacterResponse, accountId: String) {
        val sql = """
            INSERT INTO character (id, account_id, name, health, attack, stamina, defense, mana, healing, class, level, experience, should_level_up, is_owner) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
        jdbcTemplate.update(
            sql, character.id, accountId, character.name, character.health, character.attackPower,
            character.stamina, character.defensePower, character.mana, character.healingPower,
            character.characterClass.name, character.level, character.experience, character.shouldLevelUp, true
        )
    }

    fun updateCharacter(character: CharacterResponse) {
        val sql = """
            UPDATE character SET name = ?, health = ?, attack = ?, stamina = ?, defense = ?, mana = ?, healing = ?, 
            class = ?, level = ?, experience = ?, should_level_up = ? WHERE id = ?
        """
        jdbcTemplate.update(
            sql, character.name, character.health, character.attackPower, character.stamina,
            character.defensePower, character.mana, character.healingPower, character.characterClass.name,
            character.level, character.experience, character.shouldLevelUp, character.id
        )
    }

    fun getChallengers(accountId: String): List<CharacterResponse> {
        val sql = "SELECT * FROM character WHERE account_id = ?"
        return jdbcTemplate.query(sql, rowMapper, accountId)
    }

    fun getOpponents(accountId: String): List<CharacterResponse> {
        val sql = "SELECT * FROM character WHERE account_id != ?"
        return jdbcTemplate.query(sql, rowMapper, accountId)
    }
}
