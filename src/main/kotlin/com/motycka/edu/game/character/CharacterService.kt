package com.motycka.edu.game.character

import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.character.rest.CharacterRequest
import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.error.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service layer for character operations, handling business logic and validation.
 */
@Service
class CharacterService(
    private val characterRepository: CharacterRepository,
    private val accountService: AccountService
) {
    private val logger: Logger = LoggerFactory.getLogger(CharacterService::class.java)

    /**
     * Fetches all characters from the repository.
     */
    fun getAllCharacters(): List<CharacterResponse> {
        logger.info("Fetching all characters")
        return characterRepository.getAllCharacters()
    }

    /**
     * Retrieves a character by its ID. Throws NotFoundException if character does not exist.
     */
    fun getCharacterById(id: String): CharacterResponse {
        if (id.isBlank()) throw IllegalArgumentException("Character ID cannot be blank")

        logger.info("Fetching character with ID={}", id)
        return characterRepository.getCharacterById(id)
            ?: throw NotFoundException("Character with ID $id not found").also {
                logger.warn("Character with ID={} not found", id)
            }
    }

    /**
     * Creates a new character for the currently authenticated account.
     */
    fun createCharacter(request: CharacterRequest): CharacterResponse {
        val accountId = accountService.getCurrentAccountId().toString()
        logger.info("Creating new character for accountId: {}", accountId)

        // Validate request fields
        validateCharacterRequest(request)

        // Generate a unique ID for the character
        val id = (System.currentTimeMillis() / 1000).toString()

        val newCharacter = CharacterResponse(
            id = id,
            name = request.name,
            health = request.health.coerceAtLeast(0), // Ensure health is not negative
            attackPower = request.attackPower.coerceAtLeast(0),
            stamina = request.stamina?.coerceAtLeast(0),
            defensePower = request.defensePower?.coerceAtLeast(0),
            mana = request.mana?.coerceAtLeast(0),
            healingPower = request.healingPower?.coerceAtLeast(0),
            characterClass = request.characterClass,
            level = 1,
            experience = 0,
            shouldLevelUp = false,
            isOwner = true,
            isVictor = false
        )

        characterRepository.createCharacter(newCharacter, accountId)
        logger.info("Character created successfully: {}", newCharacter)

        return newCharacter
    }

    /**
     * Updates an existing character's attributes. Throws NotFoundException if character is not found.
     */
    fun updateCharacter(id: String, request: CharacterRequest): CharacterResponse {
        if (id.isBlank()) throw IllegalArgumentException("Character ID cannot be blank")

        logger.info("Updating character with ID={}", id)
        val character = getCharacterById(id)

        validateCharacterRequest(request)

        val updatedCharacter = character.copy(
            name = request.name,
            health = request.health.coerceAtLeast(0),
            attackPower = request.attackPower.coerceAtLeast(0),
            stamina = request.stamina?.coerceAtLeast(0),
            defensePower = request.defensePower?.coerceAtLeast(0),
            mana = request.mana?.coerceAtLeast(0),
            healingPower = request.healingPower?.coerceAtLeast(0)
        )

        characterRepository.updateCharacter(updatedCharacter)
        logger.info("Character updated: {}", updatedCharacter)
        return updatedCharacter
    }

    /**
     * Retrieves all characters owned by the currently authenticated user.
     */
    fun getChallengers(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId().toString()
        logger.info("Fetching all challengers for accountId: {}", accountId)

        val challengers = characterRepository.getChallengers(accountId)
        logger.info("Found {} challengers", challengers.size)

        return challengers
    }

    /**
     * Retrieves all characters that are NOT owned by the currently authenticated user.
     */
    fun getOpponents(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId().toString()
        logger.info("Fetching all opponents for accountId: {}", accountId)

        val opponents = characterRepository.getOpponents(accountId)
        logger.info("Found {} opponents", opponents.size)

        return opponents
    }

    /**
     * Updates the experience of a character by adding gained experience points.
     */
    fun updateExperience(characterId: String, experienceGained: Int) {
        if (characterId.isBlank()) throw IllegalArgumentException("Character ID cannot be blank")
        if (experienceGained < 0) throw IllegalArgumentException("Experience gained cannot be negative")

        logger.info("Updating experience for character ID={}, XP Gained={}", characterId, experienceGained)

        val character = getCharacterById(characterId)
        val updatedExperience = character.experience + experienceGained

        characterRepository.updateCharacterExperience(characterId, updatedExperience)

        logger.info("Character {} now has {} total experience points", character.name, updatedExperience)
    }

    /**
     * Validates character request inputs.
     */
    private fun validateCharacterRequest(request: CharacterRequest) {
        require(request.name.isNotBlank()) { "Character name cannot be blank" }
        require(request.health >= 0) { "Character health must be 0 or higher" }
        require(request.attackPower >= 0) { "Attack power must be 0 or higher" }
        request.stamina?.let { require(it >= 0) { "Stamina must be 0 or higher" } }
        request.defensePower?.let { require(it >= 0) { "Defense power must be 0 or higher" } }
        request.mana?.let { require(it >= 0) { "Mana must be 0 or higher" } }
        request.healingPower?.let { require(it >= 0) { "Healing power must be 0 or higher" } }
    }
}
