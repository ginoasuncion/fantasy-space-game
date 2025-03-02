package com.motycka.edu.game.character

import com.motycka.edu.game.character.rest.CharacterRequest
import com.motycka.edu.game.character.rest.CharacterResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

/**
 * Controller class for managing character-related operations.
 */
@RestController
@RequestMapping("/api/characters")
class CharacterController(private val characterService: CharacterService) {

    private val logger: Logger = LoggerFactory.getLogger(CharacterController::class.java)

    /**
     * Fetches all characters in the system.
     */
    @GetMapping
    fun getAllCharacters(): ResponseEntity<List<CharacterResponse>> {
        logger.info("Fetching all characters")
        return try {
            val characters = characterService.getAllCharacters()
            ResponseEntity.ok(characters)
        } catch (e: Exception) {
            logger.error("Error fetching characters: ${e.message}")
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve characters", e)
        }
    }

    /**
     * Fetches a single character by its ID.
     */
    @GetMapping("/{id}")
    fun getCharacterById(@PathVariable id: String): ResponseEntity<CharacterResponse> {
        logger.info("Fetching character with ID={}", id)
        return try {
            val character = characterService.getCharacterById(id)
            ResponseEntity.ok(character)
        } catch (e: IllegalArgumentException) {
            logger.warn("Character with ID={} not found", id)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found", e)
        } catch (e: Exception) {
            logger.error("Error fetching character with ID={}: {}", id, e.message)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving character", e)
        }
    }

    /**
     * Creates a new character.
     */
    @PostMapping
    fun createCharacter(@RequestBody request: CharacterRequest): ResponseEntity<CharacterResponse> {
        logger.info("Received request to create character")
        return try {
            val newCharacter = characterService.createCharacter(request)
            ResponseEntity.status(HttpStatus.CREATED).body(newCharacter)
        } catch (e: IllegalArgumentException) {
            logger.warn("Validation error: {}", e.message)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
        } catch (e: Exception) {
            logger.error("Error creating character: {}", e.message)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Character creation failed", e)
        }
    }

    /**
     * Updates an existing character.
     */
    @PutMapping("/{id}")
    fun updateCharacter(@PathVariable id: String, @RequestBody request: CharacterRequest): ResponseEntity<CharacterResponse> {
        logger.info("Updating character with ID={}", id)
        return try {
            val updatedCharacter = characterService.updateCharacter(id, request)
            ResponseEntity.ok(updatedCharacter)
        } catch (e: IllegalArgumentException) {
            logger.warn("Character update validation error: {}", e.message)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
        } catch (e: Exception) {
            logger.error("Error updating character with ID={}: {}", id, e.message)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Character update failed", e)
        }
    }

    /**
     * Fetches all challengers associated with the current user.
     */
    @GetMapping("/challengers")
    fun getChallengers(): ResponseEntity<List<CharacterResponse>> {
        return try {
            val challengers = characterService.getChallengers()
            ResponseEntity.ok(challengers)
        } catch (e: Exception) {
            logger.error("Error fetching challengers: {}", e.message)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve challengers", e)
        }
    }

    /**
     * Fetches all opponents available for battles.
     */
    @GetMapping("/opponents")
    fun getOpponents(): ResponseEntity<List<CharacterResponse>> {
        return try {
            val opponents = characterService.getOpponents()
            ResponseEntity.ok(opponents)
        } catch (e: Exception) {
            logger.error("Error fetching opponents: {}", e.message)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve opponents", e)
        }
    }
}
