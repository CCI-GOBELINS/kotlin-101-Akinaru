package com.android.one

enum class CharacterType(val maxHp: Int, val weaponName: String, val weaponPower: Int, val canHeal: Boolean) {
    Warrior(120, "Sword", 25, false),
    Magus(140, "Staff", 15, true),
    Colossus(180, "Hammer", 20, false),
    Dwarf(90, "Axe", 35, false)
}

data class Weapon(val name: String, val power: Int)

class Character(val type: CharacterType, val name: String) {
    val weapon = Weapon(type.weaponName, type.weaponPower)
    var hp: Int = type.maxHp
        private set

    val isAlive: Boolean
        get() = hp > 0

    fun attack(target: Character): Int {
        if (!isAlive || !target.isAlive) return 0
        val damage = weapon.power
        target.hp = (target.hp - damage).coerceAtLeast(0)
        return damage
    }

    fun heal(target: Character): Int {
        if (!isAlive || !type.canHeal || !target.isAlive) return 0
        val amount = weapon.power
        target.hp = (target.hp + amount).coerceAtMost(target.type.maxHp)
        return amount
    }

    override fun toString(): String {
        val status = if (isAlive) "Alive" else "Dead"
        return "$name (${type.name}) - HP: $hp/${type.maxHp} - Weapon: ${weapon.name}(${weapon.power}) - $status"
    }
}

class Player(val name: String, val team: MutableList<Character>) {
    fun aliveCharacters(): List<Character> = team.filter { it.isAlive }
    fun isDefeated(): Boolean = aliveCharacters().isEmpty()
}

fun readNonBlank(prompt: String): String {
    while (true) {
        println(prompt)
        val input = readln().trim()
        if (input.isNotEmpty()) return input
        println("Invalid input.")
    }
}

fun readIntChoice(prompt: String, min: Int, max: Int): Int {
    while (true) {
        println(prompt)
        val input = readln().trim().toIntOrNull()
        if (input != null && input in min..max) return input
        println("Choose a number between $min and $max.")
    }
}

fun chooseType(usedTypes: Set<CharacterType>): CharacterType {
    while (true) {
        println("Choose type:")
        CharacterType.entries.forEachIndexed { index, type ->
            val marker = if (type in usedTypes) " (already used)" else ""
            println("${index + 1}. ${type.name} HP=${type.maxHp} ATK=${type.weaponPower}$marker")
        }
        val choice = readIntChoice("Type number:", 1, CharacterType.entries.size)
        val type = CharacterType.entries[choice - 1]
        if (type !in usedTypes) return type
        println("This type is already in your team.")
    }
}

fun createPlayer(playerIndex: Int, globalNames: MutableSet<String>): Player {
    val playerName = readNonBlank("Player $playerIndex name:")
    val team = mutableListOf<Character>()
    val usedTypes = mutableSetOf<CharacterType>()
    repeat(3) { slot ->
        println("Create character ${slot + 1} for $playerName")
        val type = chooseType(usedTypes)
        var charName: String
        while (true) {
            charName = readNonBlank("Character name:")
            if (!globalNames.contains(charName.lowercase())) break
            println("Name already used in this game.")
        }
        globalNames.add(charName.lowercase())
        usedTypes.add(type)
        team.add(Character(type, charName))
    }
    return Player(playerName, team)
}

fun chooseCharacter(player: Player, prompt: String): Character {
    val alive = player.aliveCharacters()
    while (true) {
        println(prompt)
        alive.forEachIndexed { index, character ->
            println("${index + 1}. $character")
        }
        val choice = readIntChoice("Character number:", 1, alive.size)
        return alive[choice - 1]
    }
}

fun printTeamStatus(player: Player) {
    println("Team ${player.name}:")
    player.team.forEach { println("- $it") }
}

fun main() {
    println("Battle Arena")
    val usedNames = mutableSetOf<String>()
    val player1 = createPlayer(1, usedNames)
    val player2 = createPlayer(2, usedNames)

    var turn = 1
    while (!player1.isDefeated() && !player2.isDefeated()) {
        val activePlayer = if (turn % 2 == 1) player1 else player2
        val enemyPlayer = if (turn % 2 == 1) player2 else player1

        println("\nTurn $turn")
        printTeamStatus(player1)
        printTeamStatus(player2)

        val actor = chooseCharacter(activePlayer, "${activePlayer.name}, choose your character:")
        val canHeal = actor.type.canHeal && activePlayer.aliveCharacters().any { it.hp < it.type.maxHp }
        val action = if (canHeal) {
            readIntChoice("Choose action: 1) Attack 2) Heal", 1, 2)
        } else {
            1
        }

        if (action == 2) {
            val target = chooseCharacter(activePlayer, "Choose ally to heal:")
            val healed = actor.heal(target)
            println("${actor.name} healed ${target.name} for $healed HP.")
        } else {
            val target = chooseCharacter(enemyPlayer, "Choose enemy to attack:")
            val damage = actor.attack(target)
            println("${actor.name} attacked ${target.name} for $damage damage.")
            if (!target.isAlive) {
                println("${target.name} is dead.")
            }
        }

        turn++
    }

    val winner = if (player1.isDefeated()) player2 else player1
    println("\nGame Over")
    println("Winner: ${winner.name}")
    println("Total turns: ${turn - 1}")
    printTeamStatus(player1)
    printTeamStatus(player2)
}
