package com.motycka.edu.game.character.model

enum class CharacterClass {
    WARRIOR, SORCERER
}

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
)
