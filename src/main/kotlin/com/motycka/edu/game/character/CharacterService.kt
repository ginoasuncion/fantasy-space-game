package com.motycka.edu.game.character

import com.motycka.edu.game.character.repository.CharacterRepository
import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.character.rest.CharacterRequest
import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.error.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CharacterService(
    private val characterRepository: CharacterRepository,
    private val accountService: AccountService
) {
    private val logger: Logger = LoggerFactory.getLogger(CharacterService::class.java)

    fun getAllCharacters(): List<CharacterResponse> {
        logger.info("Fetching all characters")
        return characterRepository.getAllCharacters()
    }

    fun getCharacterById(id: String): CharacterResponse {
        logger.info("Fetching character with ID={}", id)
        return characterRepository.getCharacterById(id)
            ?: throw NotFoundException("Character with ID $id not found").also {
                logger.warn("Character with ID={} not found", id)
            }
    }

    fun createCharacter(request: CharacterRequest): CharacterResponse {
        val accountId = accountService.getCurrentAccountId().toString()
        logger.info("Creating new character for accountId: {}", accountId)

        val id = (System.currentTimeMillis() / 1000).toString()
        val newCharacter = CharacterResponse(
            id = id,
            name = request.name,
            health = request.health,
            attackPower = request.attackPower,
            stamina = request.stamina,
            defensePower = request.defensePower,
            mana = request.mana,
            healingPower = request.healingPower,
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

    fun updateCharacter(id: String, request: CharacterRequest): CharacterResponse {
        logger.info("Updating character with ID={}", id)
        val character = getCharacterById(id)
        val updatedCharacter = character.copy(
            name = request.name,
            health = request.health,
            attackPower = request.attackPower,
            stamina = request.stamina,
            defensePower = request.defensePower,
            mana = request.mana,
            healingPower = request.healingPower
        )
        characterRepository.updateCharacter(updatedCharacter)
        logger.info("Character updated: {}", updatedCharacter)
        return updatedCharacter
    }

    fun getChallengers(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId().toString()
        logger.info("Fetching all challengers for accountId: {}", accountId)
        val challengers = characterRepository.getChallengers(accountId)
        logger.info("Found {} challengers", challengers.size)
        return challengers
    }

    fun getOpponents(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId().toString()
        logger.info("Fetching all opponents for accountId: {}", accountId)
        val opponents = characterRepository.getOpponents(accountId)
        logger.info("Found {} opponents", opponents.size)
        return opponents
    }
}
