package com.motycka.edu.game.leaderboard.repository

import com.motycka.edu.game.leaderboard.model.LeaderboardEntry
import com.motycka.edu.game.leaderboard.rest.LeaderboardResponse
import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.character.model.CharacterClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class LeaderboardRepository(private val jdbcTemplate: JdbcTemplate) {

    private val logger: Logger = LoggerFactory.getLogger(LeaderboardRepository::class.java)

    private val rowMapper = RowMapper { rs, _ ->
        LeaderboardEntry(
            position = rs.getInt("position"),
            character = CharacterResponse(
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
                isOwner = false
            ),
            wins = rs.getInt("wins"),
            losses = rs.getInt("losses"),
            draws = rs.getInt("draws")
        )
    }

    /**
     * Retrieves the leaderboard, optionally filtering by character class.
     * Leaderboard is sorted by:
     *   - Wins (Descending)
     *   - Losses (Ascending)
     *   - Draws (Descending)
     */
    fun getLeaderboard(classFilter: String?): List<LeaderboardResponse> {
        logger.info("Fetching leaderboard with class filter: {}", classFilter ?: "None")
        return try {
            val sql = """
            SELECT 
                ROW_NUMBER() OVER (ORDER BY l.wins DESC, l.losses ASC, l.draws DESC) AS position,
                c.*, 
                l.wins, l.losses, l.draws
            FROM leaderboard l
            JOIN character c ON l.character_id = c.id
            ${if (classFilter != null) "WHERE c.class = ?" else ""}
        """.trimIndent()

            val leaderboardEntries: List<LeaderboardEntry> = if (classFilter != null) {
                jdbcTemplate.query(sql, rowMapper, classFilter)
            } else {
                jdbcTemplate.query(sql, rowMapper)
            }

            leaderboardEntries.map { entry ->
                LeaderboardResponse(
                    position = entry.position,
                    character = entry.character,
                    wins = entry.wins,
                    losses = entry.losses,
                    draws = entry.draws
                )
            }
        } catch (ex: DataAccessException) {
            logger.error("Error retrieving leaderboard: {}", ex.message)
            emptyList()
        }
    }

    /**
     * Updates the leaderboard after a match.
     * - If a character is already on the leaderboard, updates their stats.
     * - If a character is new, inserts a new record.
     */
    fun updateLeaderboard(winnerId: String?, loserId: String?, isDraw: Boolean) {
        try {
            if (isDraw) {
                logger.info("Updating leaderboard for a DRAW match between {} and {}", winnerId, loserId)

                val sqlDraw = """
                    MERGE INTO leaderboard USING (VALUES (?)) AS vals(character_id)
                    ON leaderboard.character_id = vals.character_id
                    WHEN MATCHED THEN 
                        UPDATE SET draws = draws + 1
                    WHEN NOT MATCHED THEN
                        INSERT (character_id, wins, losses, draws) VALUES (?, 0, 0, 1);
                """
                jdbcTemplate.update(sqlDraw, winnerId, winnerId)
                jdbcTemplate.update(sqlDraw, loserId, loserId)
            } else {
                logger.info("Updating leaderboard. Winner: {}, Loser: {}", winnerId, loserId)

                val sqlWinner = """
                    MERGE INTO leaderboard USING (VALUES (?)) AS vals(character_id)
                    ON leaderboard.character_id = vals.character_id
                    WHEN MATCHED THEN 
                        UPDATE SET wins = wins + 1
                    WHEN NOT MATCHED THEN
                        INSERT (character_id, wins, losses, draws) VALUES (?, 1, 0, 0);
                """
                jdbcTemplate.update(sqlWinner, winnerId, winnerId)

                val sqlLoser = """
                    MERGE INTO leaderboard USING (VALUES (?)) AS vals(character_id)
                    ON leaderboard.character_id = vals.character_id
                    WHEN MATCHED THEN 
                        UPDATE SET losses = losses + 1
                    WHEN NOT MATCHED THEN
                        INSERT (character_id, wins, losses, draws) VALUES (?, 0, 1, 0);
                """
                jdbcTemplate.update(sqlLoser, loserId, loserId)
            }
        } catch (ex: DataAccessException) {
            logger.error("Error updating leaderboard: {}", ex.message)
        }
    }
}
