package com.motycka.edu.game.character

import com.motycka.edu.game.character.rest.CharacterRequest
import com.motycka.edu.game.character.rest.CharacterResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/characters")
class CharacterController(private val characterService: CharacterService) {

    private val logger: Logger = LoggerFactory.getLogger(CharacterController::class.java)

    @GetMapping
    fun getAllCharacters(): ResponseEntity<List<CharacterResponse>> {
        logger.info("Fetching all characters")
        val characters = characterService.getAllCharacters()
        return ResponseEntity.ok(characters)
    }

    @GetMapping("/{id}")
    fun getCharacterById(@PathVariable id: String): ResponseEntity<CharacterResponse> {
        logger.info("Fetching character with ID={}", id)
        return ResponseEntity.ok(characterService.getCharacterById(id))
    }

    @PostMapping
    fun createCharacter(@RequestBody request: CharacterRequest): ResponseEntity<CharacterResponse> {
        logger.info("Received request to create character")
        return ResponseEntity.ok(characterService.createCharacter(request))
    }

    @PutMapping("/{id}")
    fun updateCharacter(@PathVariable id: String, @RequestBody request: CharacterRequest): ResponseEntity<CharacterResponse> {
        return ResponseEntity.ok(characterService.updateCharacter(id, request))
    }

    @GetMapping("/challengers")
    fun getChallengers(): ResponseEntity<List<CharacterResponse>> {
        return ResponseEntity.ok(characterService.getChallengers())
    }

    @GetMapping("/opponents")
    fun getOpponents(): ResponseEntity<List<CharacterResponse>> {
        return ResponseEntity.ok(characterService.getOpponents())
    }
}
