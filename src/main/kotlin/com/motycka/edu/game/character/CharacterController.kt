package com.motycka.edu.game.character

import com.motycka.edu.game.character.rest.CharacterRequest
import com.motycka.edu.game.character.rest.CharacterResponse
import com.motycka.edu.game.character.model.CharacterClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

/**
 * Controller for handling character-related operations.
 */
@RestController
@RequestMapping("/api/characters")
class CharacterController(private val characterService: CharacterService) {

    private val logger: Logger = LoggerFactory.getLogger(CharacterController::class.java)

    /**
     * Retrieves all characters.
     * Supports filtering by class (`WARRIOR`, `SORCERER`) and name.
     */
    @GetMapping
    fun getAllCharacters(
        @RequestParam("class", required = false) characterClass: CharacterClass?,
        @RequestParam(required = false) name: String?
    ): ResponseEntity<List<CharacterResponse>> {
        logger.info("Fetching all characters - Filter: class={}, name={}", characterClass, name)
        return try {
            val characters = characterService.getAllCharacters(characterClass, name)
            ResponseEntity.ok(characters)
        } catch (e: Exception) {
            logger.error("Error fetching characters: ${e.message}", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve characters", e)
        }
    }

    /**
     * Retrieves a character by ID.
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
            logger.error("Error retrieving character with ID={}: {}", id, e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving character", e)
        }
    }

    /**
     * Creates a new character.
     */
    @PostMapping
    fun createCharacter(@RequestBody request: CharacterRequest): ResponseEntity<CharacterResponse> {
        logger.info("Creating new character: {}", request.name)
        return try {
            val newCharacter = characterService.createCharacter(request)
            ResponseEntity.status(HttpStatus.CREATED).body(newCharacter)
        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid character creation request: {}", e.message)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
        } catch (e: Exception) {
            logger.error("Character creation failed: {}", e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Character creation failed", e)
        }
    }

    /**
     * Updates an existing character by ID.
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
            logger.error("Error updating character with ID={}: {}", id, e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Character update failed", e)
        }
    }

    /**
     * Retrieves challengers owned by the current user.
     */
    @GetMapping("/challengers")
    fun getChallengers(): ResponseEntity<List<CharacterResponse>> {
        return try {
            val challengers = characterService.getChallengers()
            ResponseEntity.ok(challengers)
        } catch (e: Exception) {
            logger.error("Error fetching challengers: {}", e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve challengers", e)
        }
    }

    /**
     * Retrieves available opponents for battles.
     */
    @GetMapping("/opponents")
    fun getOpponents(): ResponseEntity<List<CharacterResponse>> {
        return try {
            val opponents = characterService.getOpponents()
            ResponseEntity.ok(opponents)
        } catch (e: Exception) {
            logger.error("Error fetching opponents: {}", e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve opponents", e)
        }
    }
}
