package com.motycka.edu.game.character.rest

import com.motycka.edu.game.character.model.CharacterClass

/**
 * Data class representing the request to create or update a character.
 */
data class CharacterRequest(
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int?,
    val defensePower: Int?,
    val mana: Int?,
    val healingPower: Int?,
    val characterClass: CharacterClass
) {
    init {
        require(name.isNotBlank()) { "Character name cannot be blank." }
        require(health >= 0) { "Health cannot be negative." }
        require(attackPower >= 0) { "Attack power cannot be negative." }

        // Validations based on character class
        when (characterClass) {
            CharacterClass.WARRIOR -> {
                require(stamina != null) { "Warriors must have stamina." }
                require(defensePower != null) { "Warriors must have defense power." }
            }
            CharacterClass.SORCERER -> {
                require(mana != null) { "Sorcerers must have mana." }
                require(healingPower != null) { "Sorcerers must have healing power." }
            }
        }
    }
}

/**
 * Data class representing the response when fetching a character.
 */
data class CharacterResponse(
    val id: String,
    val name: String,
    var health: Int,
    val stamina: Int?,
    val attackPower: Int,
    var defensePower: Int?,
    val mana: Int?,
    val healingPower: Int?,
    val characterClass: CharacterClass,
    val level: Int,
    val experience: Int,
    val shouldLevelUp: Boolean,
    val isOwner: Boolean,
    val isVictor: Boolean = false,
    val experienceGained: Int = 0
)
