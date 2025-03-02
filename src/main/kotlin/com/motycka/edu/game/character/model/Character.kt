package com.motycka.edu.game.character.model

/**
 * Enum representing the available character classes.
 */
enum class CharacterClass {
    WARRIOR, SORCERER
}

/**
 * Data class representing a game character.
 */
data class Character(
    val id: String,
    val name: String,
    var health: Int,
    var attackPower: Int,
    var stamina: Int?,
    var defensePower: Int?,
    var mana: Int?,
    var healingPower: Int?,
    var characterClass: CharacterClass,
    var level: Int,
    var experience: Int,
    var shouldLevelUp: Boolean,
    var isOwner: Boolean
) {
    init {
        require(health >= 0) { "Health cannot be negative." }
        require(attackPower >= 0) { "Attack power cannot be negative." }
        require(level >= 1) { "Level must be at least 1." }
        require(experience >= 0) { "Experience cannot be negative." }

        // Additional checks based on character class
        if (characterClass == CharacterClass.WARRIOR) {
            require(stamina != null) { "Warriors must have stamina." }
            require(defensePower != null) { "Warriors must have defense power." }
        }
        if (characterClass == CharacterClass.SORCERER) {
            require(mana != null) { "Sorcerers must have mana." }
            require(healingPower != null) { "Sorcerers must have healing power." }
        }
    }
}
