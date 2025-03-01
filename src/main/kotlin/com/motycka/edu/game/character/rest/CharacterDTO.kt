package com.motycka.edu.game.character.rest
import com.motycka.edu.game.character.model.CharacterClass

data class CharacterRequest(
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int?,
    val defensePower: Int?,
    val mana: Int?,
    val healingPower: Int?,
    val characterClass: CharacterClass
)

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


